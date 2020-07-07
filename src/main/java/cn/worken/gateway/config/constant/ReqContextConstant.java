package cn.worken.gateway.config.constant;

/**
 * @author shaoyijiong
 * @date 2020/7/6
 */
public interface ReqContextConstant {

    /**
     * 用户解析jwt
     */
    String SECURITY_INFO_IN_REQ = "SECURITY_INFO_IN_REQ";
    /**
     * 该token是 平台用户 还是 client
     */
    String SECURITY_IS_USER = "SECURITY_IS_USER";
    /**
     * 用户信息
     */
    String GATEWAY_AUTHENTICATION_INFO = "GATEWAY_AUTHENTICATION_INFO";
    /**
     * 是否为白名单ip
     */
    String IS_WHITE_API = "IS_WHITE_API";
}
