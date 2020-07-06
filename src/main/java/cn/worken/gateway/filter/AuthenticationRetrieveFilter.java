package cn.worken.gateway.filter;

import cn.worken.gateway.config.constant.ClientConstants;
import cn.worken.gateway.config.constant.ReqContextConstant;
import cn.worken.gateway.config.constant.UserConstants;
import cn.worken.gateway.dto.GatewayAuthenticationInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 用户认证信息解析
 *
 * @author shaoyijiong
 * @date 2020/7/6
 */
@Component
@Slf4j
public class AuthenticationRetrieveFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // TODO 判断是否为白名单id
        Boolean isUser = exchange.getAttribute(ReqContextConstant.SECURITY_IS_USER);
        Jwt jwt = exchange.getAttribute(ReqContextConstant.SECURITY_INFO_IN_REQ);
        if (jwt == null || isUser == null) {
            return chain.filter(exchange);
        }
        GatewayAuthenticationInfo authenticationInfo;
        if (isUser) {
            authenticationInfo = GatewayAuthenticationInfo.builder()
                .userId(String.valueOf(jwt.<Integer>getClaim(UserConstants.USER_ID)))
                .clientId(jwt.getClaim(UserConstants.CLIENT_ID))
                .comId(jwt.getClaim(UserConstants.COM_ID))
                .username(jwt.getClaim(UserConstants.USER_NAME))
                .name(jwt.getClaim(UserConstants.NAME))
                .userType(jwt.getClaim(UserConstants.USER_TYPE))
                .build();
        } else {
            authenticationInfo = GatewayAuthenticationInfo.builder()
                .clientId(jwt.getClaim(ClientConstants.CLIENT_ID))
                .comId(jwt.getClaim(ClientConstants.COM_ID))
                .build();
        }
        // attribute 存入用户信息
        exchange.getAttributes().put(ReqContextConstant.GATEWAY_AUTHENTICATION_INFO, authenticationInfo);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return GlobalFilterOrders.AUTHENTICATION.getOrder();
    }
}
