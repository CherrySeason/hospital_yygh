package com.atguigu.hospital.service;

import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

//@Transactional(rollbackFor = Exception.class)
public interface HospitalService {

    /**
     * 预约下单
     *
     * @param paramMap
     * @return
     */
    Map<String, Object> submitOrder(Map<String, Object> paramMap);

    /**
     * 更新支付状态
     *
     * @param paramMap
     */
    void updatePayStatus(Map<String, Object> paramMap);

    /**
     * 更新取消预约状态
     *
     * @param paramMap
     */
    void updateCancelStatus(Map<String, Object> paramMap);


}
