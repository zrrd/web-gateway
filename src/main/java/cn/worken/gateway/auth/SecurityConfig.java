package cn.worken.gateway.auth;

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
                        .flatMap(authToken -> jwtDecoder().decode(authToken.getToken()))
                        .map(jwt -> new AuthorizationDecision(true))
                        .onErrorReturn((throwable) -> {
                            log.error("", throwable);
                            return true;
                        }, new AuthorizationDecision(false));
                }
            )
            .and().build();

    }
}
