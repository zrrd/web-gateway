package cn.worken.gateway.auth;

import cn.worken.gateway.config.constant.ReqContextConstant;
import cn.worken.gateway.config.constant.UserConstants;
import cn.worken.gateway.util.RSAUtils;
import com.google.common.io.CharStreams;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.interfaces.RSAPublicKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.ResourceUtils;
import reactor.core.publisher.Mono;

/**
 * 鉴权判断
 *
 * @author shaoyijiong
 * @date 2020/7/4
 */
@Slf4j
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * rsa 加密 key
     */
    @SneakyThrows
    @Bean
    public NimbusReactiveJwtDecoder jwtDecoder() {
        File pubFile = ResourceUtils.getFile("classpath:pub.key");
        String pubString = CharStreams.toString(new InputStreamReader(new FileInputStream(pubFile)));
        RSAPublicKey publicKey = RSAUtils.getPublicKey(pubString);
        return new NimbusReactiveJwtDecoder(publicKey);
    }


    /**
     * 鉴权配置
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        // 允许端点监控
        return http.csrf().disable()
            .authorizeExchange().pathMatchers("/actuator/**").permitAll()
            // 白名单
            .and().authorizeExchange().pathMatchers("/oauth/**").permitAll()
            // 其他所有接口需要鉴权
            .and().authorizeExchange().anyExchange().access((authentication, object) -> {
                // 获取token
                return new ServerBearerTokenAuthenticationConverter().convert(object.getExchange())
                    .map(auth -> (BearerTokenAuthenticationToken) auth)
                    // 校验 jwt token
                    .flatMap(authToken -> jwtDecoder().decode(authToken.getToken()))
                    // 校验成功 , 在 attributes 中放入校验后的信息 jwt
                    .doOnSuccess(authJwt -> object.getExchange().getAttributes()
                        .put(ReqContextConstant.SECURITY_INFO_IN_REQ, authJwt))
                    // 判断是否是平台用户还是 client 请求
                    .doOnSuccess(authJwt -> object.getExchange().getAttributes()
                        .put(ReqContextConstant.SECURITY_IS_USER,
                            authJwt.getClaims().get(UserConstants.USER_NAME) != null))
                    .map(jwt -> new AuthorizationDecision(true))
                    // 校验失败 抛出异常 交给全局异常处理
                    .onErrorReturn((throwable) -> {
                        log.error("", throwable);
                        return true;
                    }, new AuthorizationDecision(false));
            })
            // 鉴权校验失败 直接抛出异常交给全局异常处理
            .and().exceptionHandling().authenticationEntryPoint((exchange, e) -> Mono.error(e))
            // 资源校验失败 直接抛出异常交给全局异常处理
            .and().exceptionHandling().accessDeniedHandler((exchange, e) -> Mono.error(e))
            .and().build();
    }
}
