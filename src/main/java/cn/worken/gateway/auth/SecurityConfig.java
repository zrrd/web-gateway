package cn.worken.gateway.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;

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

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        // 允许端点监控
        return http.csrf().disable()
            .authorizeExchange().pathMatchers("/actuator/**").permitAll()
            // 白名单
            .and().authorizeExchange().pathMatchers("/oauth/**").permitAll()
            // 其他所有接口需要鉴权
            .and().authorizeExchange().anyExchange().access((authentication, object) -> authentication.map(auth -> {
                    boolean granted = false;
                    // 获取token
                    new ServerBearerTokenAuthenticationConverter().convert(object.getExchange());
                    return (new AuthorizationDecision(true));
                })
            )
            .and().build();

    }
}
