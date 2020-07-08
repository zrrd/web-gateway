package cn.worken.gateway.filter;

import cn.worken.gateway.config.constant.ClientConstants;
import cn.worken.gateway.config.constant.GatewayTransHeader;
import cn.worken.gateway.config.constant.ReqContextConstant;
import cn.worken.gateway.config.constant.UserConstants;
import cn.worken.gateway.dto.GatewayAuthenticationInfo;
import cn.worken.gateway.resource.manage.WhiteListServerWebExchangeMatcher;
import com.alibaba.fastjson.JSON;
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

    private final WhiteListServerWebExchangeMatcher whiteListServerWebExchangeMatcher;

    public AuthenticationRetrieveFilter(WhiteListServerWebExchangeMatcher whiteListServerWebExchangeMatcher) {
        this.whiteListServerWebExchangeMatcher = whiteListServerWebExchangeMatcher;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (whiteListServerWebExchangeMatcher.isWhiteApi(exchange)) {
            return chain.filter(exchange);
        }
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
        // 请求头存入用户信息 供后续服务访问
        exchange.getRequest().mutate()
            .header(GatewayTransHeader.X_GATEWAY_AUTHENTICATION_INFO, JSON.toJSONString(authenticationInfo));
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return GlobalFilterOrders.AUTHENTICATION.getOrder();
    }
}
