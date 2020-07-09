package cn.worken.gateway.resource.adapter.user;

import cn.worken.gateway.config.constant.GatewayCode;
import cn.worken.gateway.dto.GatewayAuthenticationInfo;
import cn.worken.gateway.resource.ResourceAccessStatus;
import cn.worken.gateway.resource.ResourceAdapter;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 用户资源权限控制
 *
 * @author shaoyijiong
 * @date 2020/7/6
 */
@Slf4j
@Component
public class UserResourceAdapter implements ResourceAdapter<UserApiResource> {

    private final UserApiResourceMapping userApiResourceMapping;
    private final StringRedisTemplate stringRedisTemplate;
    private final String redisResPrefix;

    public UserResourceAdapter(UserApiResourceMapping userApiResourceMapping, StringRedisTemplate stringRedisTemplate) {
        this.userApiResourceMapping = userApiResourceMapping;
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisResPrefix = "oauth:res:";
    }

    @Override
    public Mono<UserApiResource> loadResource(String apiId) {
        return Mono.empty();
    }

    @Override
    public Mono<UserApiResource> loadResourceByReqUri(String serviceId, String reqUri) {
        UserApiResource userApiResource = userApiResourceMapping.getUserApiResource(serviceId, reqUri);
        if (null == userApiResource) {
            userApiResource = new UserApiResource();
            userApiResource.setApiId("");
            userApiResource.setResourceName(reqUri);
        }
        return Mono.just(userApiResource);
    }

    @Override
    public Mono<ResourceAccessStatus> access(GatewayAuthenticationInfo authenticationInfo,
        Mono<UserApiResource> apiResource) {
        return apiResource.map(r -> {
            ResourceAccessStatus resourceAccessStatus = new ResourceAccessStatus();
            if (r == null || r.getApiId() == null) {
                resourceAccessStatus.setAccess(true);
            } else if (remoteCheckApiAccess(authenticationInfo.getUserId(), r.getApiId(), r.getResourceName())) {
                resourceAccessStatus.setAccess(true);
            } else {
                resourceAccessStatus.setAccess(false);
                resourceAccessStatus.setDenyCode(GatewayCode.ACCESS_DENY.getCode());
                resourceAccessStatus.setDenyMsg(GatewayCode.ACCESS_DENY.getMessage());
            }
            return resourceAccessStatus;
        });
    }

    /**
     * 判断用户资源是否匹配
     *
     * @param uid 用户id
     * @param apiId 资源id
     * @return 匹配
     */
    private boolean remoteCheckApiAccess(String uid, String apiId, String resourceName) {
        log.info("资源校验 , 用户id [{}] , 请求资源 [{}] , 请求接口 [{}]", uid, apiId, resourceName);
        BoundSetOperations<String, String> ops = stringRedisTemplate.boundSetOps(redisResPrefix + uid);
        return Optional.ofNullable(ops.isMember(apiId)).orElse(Boolean.FALSE);
    }
}
