package cn.worken.gateway.resource;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;


import cn.worken.gateway.dto.GatewayAuthenticationInfo;
import cn.worken.gateway.util.RouteUtils;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author xuanlubin
 * @version 1.0
 * @date 2019/4/4 12:39
 */
public interface ResourceAdapter<T extends ApiResource> {

    default T loadResource(ServerWebExchange exchange) {
        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        if (route == null) {
            return null;
        }
        String serviceId = RouteUtils.getLbName(route);
        T apiResource = this.loadResourceByReqUri(serviceId, exchange.getRequest().getPath().value());
        if (null != apiResource) {
            apiResource.setServiceName(serviceId);
        }
        return apiResource;
    }


    /**
     * 根据资源id加载api资源
     *
     * @param apiId 资源id
     * @return api资源
     */
    T loadResource(String apiId);

    /**
     * 根据请求URL加载api资源
     *
     * @param serviceId 服务ID
     * @param reqUri 资源Uri
     * @return api资源
     */
    T loadResourceByReqUri(String serviceId, String reqUri);

    /**
     * 验证对该资源的访问权限
     *
     * @param authenticationInfo 用户/客户端标识
     * @param apiResource 访问资源
     * @return 返回访问权限检查状态
     */
    ResourceAccessStatus access(GatewayAuthenticationInfo authenticationInfo, T apiResource);
}
