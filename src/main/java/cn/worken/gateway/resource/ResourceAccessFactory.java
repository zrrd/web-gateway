package cn.worken.gateway.resource;

import cn.worken.gateway.config.constant.GatewayCode;
import cn.worken.gateway.config.exception.GatewayException;
import cn.worken.gateway.dto.GatewayAuthenticationInfo;
import cn.worken.gateway.resource.adapter.client.ClientResourceAdapter;
import cn.worken.gateway.resource.adapter.user.UserResourceAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author shaoyijiong
 * @date 2020/7/7
 */
@Component
public class ResourceAccessFactory {

    private final ClientResourceAdapter clientResourceAdapter;
    private final UserResourceAdapter userResourceAdapter;

    public ResourceAccessFactory(ClientResourceAdapter clientResourceAdapter, UserResourceAdapter userResourceAdapter) {
        this.clientResourceAdapter = clientResourceAdapter;
        this.userResourceAdapter = userResourceAdapter;
    }

    public ResourceAccessStatus access(boolean isUser, ServerWebExchange exchange, GatewayAuthenticationInfo authenticationInfo) {
        ResourceAdapter resourceAdapter = isUser ? userResourceAdapter : clientResourceAdapter;
        ApiResource apiResource = resourceAdapter.loadResource(exchange);
        if (apiResource == null) {
            throw new GatewayException(GatewayCode.API_NOT_EXIST);
        }
        return resourceAdapter.access(authenticationInfo, apiResource);
    }
}
