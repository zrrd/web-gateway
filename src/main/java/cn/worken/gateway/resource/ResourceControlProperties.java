package cn.worken.gateway.resource;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xuanlubin
 * @version 1.0
 * @date 2020/1/7 16:23
 */
@Slf4j
@Data
@Component
@ConfigurationProperties("resource")
public class ResourceControlProperties {

    /**
     * 不作为用户访问接口访问的服务
     */
    private Set<String> excludeResourceServiceList = new HashSet<>();

    /**
     * 白名单列表
     */
    private Set<String> whiteApiList = new HashSet<>();

    /**
     * 用户黑名单列表
     */
    private Set<String> blockApiList = new HashSet<>();


    @PostConstruct
    private void init() {
        log.info("网关uri控制列表------>\nexcludeResourceServiceList:[{}]\nwhiteApiList:[{}]\nblockApiList:[{}]",
            excludeResourceServiceList, whiteApiList, blockApiList);
    }
}
