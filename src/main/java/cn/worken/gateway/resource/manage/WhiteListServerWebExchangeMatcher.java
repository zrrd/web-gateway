package cn.worken.gateway.resource.manage;


import cn.worken.gateway.config.constant.ReqContextConstant;
import cn.worken.gateway.resource.ResourceControl;
import cn.worken.gateway.util.GatewayUtils;
import javax.annotation.Resource;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author shaoyijong
 * @version 1.0
 * @date 2019/4/8 17:03
 */
@Component
public class WhiteListServerWebExchangeMatcher implements ServerWebExchangeMatcher {

    @Resource
    private ResourceControl resourceControl;


    /**
     * 白名单  不需要认证校验认证信息
     */
    @Override
    public Mono<MatchResult> matches(ServerWebExchange exchange) {
        String path = GatewayUtils.getRawPath(exchange);
        if (testWhitelist(path)) {
            exchange.getAttributes().put(ReqContextConstant.IS_WHITE_API, true);
            return MatchResult.match();
        } else {
            return MatchResult.notMatch();
        }
    }

    private boolean testWhitelist(String path) {
        return resourceControl.isWhiteApi(path);
    }

    public boolean isWhiteApi(ServerWebExchange exchange) {
        return exchange.getAttributeOrDefault(ReqContextConstant.IS_WHITE_API, false);
    }
}
