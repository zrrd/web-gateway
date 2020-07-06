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
}
