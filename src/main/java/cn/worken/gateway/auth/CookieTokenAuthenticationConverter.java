package cn.worken.gateway.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author shaoyijiong
 * @date 2020/7/7
 */
public class CookieTokenAuthenticationConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange serverWebExchange) {
        return Mono.justOrEmpty(serverWebExchange.getRequest())
            .map(r -> r.getCookies().getFirst("token"))
            .map(cookie -> new BearerTokenAuthenticationToken(cookie.getValue()));
    }
}
