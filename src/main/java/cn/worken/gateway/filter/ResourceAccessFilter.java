package cn.worken.gateway.filter;

import cn.worken.gateway.config.constant.ReqContextConstant;
import cn.worken.gateway.config.exception.GatewayException;
import cn.worken.gateway.dto.GatewayAuthenticationInfo;
import cn.worken.gateway.resource.ResourceAccessStatus;
import cn.worken.gateway.resource.adapter.user.UserApiResource;
import cn.worken.gateway.resource.adapter.user.UserResourceAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 资源拦截
 *
 * @author shaoyijiong
 * @date 2020/7/6
 */
@Component
@Slf4j
public class ResourceAccessFilter implements GlobalFilter, Ordered {

    private final UserResourceAdapter userResourceAdapter;

    public ResourceAccessFilter(UserResourceAdapter userResourceAdapter) {
        this.userResourceAdapter = userResourceAdapter;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        boolean isUser = exchange.getAttributeOrDefault(ReqContextConstant.SECURITY_IS_USER, false);
        if (isUser) {
            UserApiResource apiResource = userResourceAdapter.loadResource(exchange);
            GatewayAuthenticationInfo authenticationInfo =
                exchange.getAttribute(ReqContextConstant.GATEWAY_AUTHENTICATION_INFO);
            ResourceAccessStatus resourceAccessStatus = userResourceAdapter.access(authenticationInfo, apiResource);
            if (resourceAccessStatus.isAccess()) {
                return chain.filter(exchange);
            } else {
                throw new GatewayException(resourceAccessStatus.getDenyCode(),
                    resourceAccessStatus.getDenyMsg());
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return GlobalFilterOrders.RESOURCE_ACCESS.getOrder();
    }
}
