package cn.worken.gateway.filter;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;

/**
 * @author shaoyijiong
 * @date 2020/7/6
 */
@Getter
public enum GlobalFilterOrders {

    /**
     * 认证
     */
    AUTHENTICATION(OrderStart.COUNT.getAndIncrement());

    private final int order;

    GlobalFilterOrders(int order) {
        this.order = order;
    }

    static class OrderStart {

        static final AtomicInteger COUNT = new AtomicInteger(1000);
    }

}
