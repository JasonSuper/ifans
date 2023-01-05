package com.ifans.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifans.api.order.domain.StoreOrderPaymentInfo;
import com.ifans.order.mapper.PaymentInfoMapper;
import com.ifans.order.service.PaymentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, StoreOrderPaymentInfo> implements PaymentInfoService {

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    /*@Override
    public int save(StoreOrderPaymentInfo infoEntity) {
        return paymentInfoMapper.save(infoEntity);
    }*/

    @Override
    public StoreOrderPaymentInfo findByOrderNo(String orderNo) {
        return paymentInfoMapper.findByOrderNo(orderNo);
    }
}
