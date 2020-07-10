package cn.worken.gateway.resource.adapter.client;

import cn.worken.gateway.config.constant.GatewayCode;
import cn.worken.gateway.dto.GatewayAuthenticationInfo;
import cn.worken.gateway.resource.ResourceAccessStatus;
import cn.worken.gateway.resource.ResourceAdapter;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * client 权限控制
 *
 * @author shaoyijiong
 * @date 2020/7/7
 */
@Component
public class ClientResourceAdapter implements ResourceAdapter<ClientApiResource> {

    private final DatabaseClient databaseClient;

    public ClientResourceAdapter(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<ClientApiResource> loadResource(String apiId) {
        return databaseClient
            .execute("select id AS apiId,api_uri AS apiUri from open_api where id = :apiId and status = 1")
            .bind("apiId", apiId).as(ClientApiResource.class).fetch().one();
    }

    /**
     * 查询请求url对应的资源id
     *
     * @param serviceId 服务ID
     * @param reqUri 资源Uri
     */
    @Override
    public Mono<ClientApiResource> loadResourceByReqUri(String serviceId, String reqUri) {
        String absoluteUrl = "/" + StringUtils.lowerCase(serviceId) + reqUri;
        return databaseClient.select().from("open_api").project("id", "api_uri")
            .matching(Criteria.where("api_uri").is(absoluteUrl).and(Criteria.where("status").is(1)))
            .map(row -> {
                ClientApiResource clientApiResource = new ClientApiResource();
                clientApiResource.setApiId(row.get("id", String.class));
                clientApiResource.setResourceName(row.get("api_uri", String.class));
                return clientApiResource;
            }).one();
    }

    @Override
    public Mono<ResourceAccessStatus> access(GatewayAuthenticationInfo authenticationInfo,
        Mono<ClientApiResource> apiResource) {
        // 判断该 client 拥有的资源id 是否匹配d
        return loadClientApiId(authenticationInfo.getClientId())
            // 判断匹配
            .flatMap(apiId -> apiResource.map(resource -> resource.getApiId().equals(apiId)))
            .all(Boolean::booleanValue)
            .map(has -> {
                if (has) {
                    return ResourceAccessStatus.accessSuccess();
                } else {
                    return ResourceAccessStatus
                        .accessFail(GatewayCode.ACCESS_DENY.getCode(), GatewayCode.ACCESS_DENY.getMessage());
                }
            });

    }

    /**
     * 查询客户端拥有的api
     *
     * @param appKey 客户端 client id
     */
    public Flux<String> loadClientApiId(String appKey) {
        return databaseClient.select().from("open_api_grant_rel").project("api_id")
            .matching(Criteria.where("app_key").is(appKey))
            .map(row -> row.get("api_id", String.class))
            .all();
    }
}
