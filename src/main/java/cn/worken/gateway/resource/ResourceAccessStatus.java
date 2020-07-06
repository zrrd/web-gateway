package cn.worken.gateway.resource;

import com.alibaba.fastjson.annotation.JSONField;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xuanlubin
 * @version 1.0
 * @date 2019/4/4 12:44
 */
@Getter
@Setter
public class ResourceAccessStatus implements Serializable {

    /**
     * 是否通过
     */
    @JSONField(serialize = false)
    private boolean access;
    /**
     * 拒绝响应码
     */
    private int denyCode;
    /**
     * 拒绝消息
     */
    private String denyMsg;
}
