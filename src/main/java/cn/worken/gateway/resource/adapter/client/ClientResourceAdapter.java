package cn.worken.gateway.resource.adapter.client;

import cn.worken.gateway.dto.GatewayAuthenticationInfo;
import cn.worken.gateway.resource.ResourceAccessStatus;
import cn.worken.gateway.resource.ResourceAdapter;
import org.springframework.stereotype.Component;

/**
 * client 权限控制
 *
 * @author shaoyijiong
 * @date 2020/7/7
 */
@Component
public class ClientResourceAdapter implements ResourceAdapter<ClientApiResource> {

    @Override
    public ClientApiResource loadResource(String apiId) {
        return null;
    }

    @Override
    public ClientApiResource loadResourceByReqUri(String serviceId, String reqUri) {
        return null;
    }

    @Override
    public ResourceAccessStatus access(GatewayAuthenticationInfo authenticationInfo, ClientApiResource apiResource) {
        return null;
    }
}
