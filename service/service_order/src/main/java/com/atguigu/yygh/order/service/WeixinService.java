package com.atguigu.yygh.order.service;

import java.util.Map;

public interface WeixinService {
    public Map createNative(Long orderId);

    /**
     * 根据订单号去微信第三方查询支付状态
     */
    Map<String, String> queryPayStatus(Long orderId, String paymentType);

    Boolean refund(Long orderId);

}
