package com.ifans.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifans.api.order.domain.StoreOrderPaymentInfo;
import com.ifans.api.order.domain.StoreOrderRefundinfo;
import com.ifans.order.mapper.PaymentInfoMapper;
import com.ifans.order.mapper.RefundinfoMapper;
import com.ifans.order.service.PaymentInfoService;
import com.ifans.order.service.RefundinfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefundinfoServiceImpl extends ServiceImpl<RefundinfoMapper, StoreOrderRefundinfo> implements RefundinfoService {

    @Autowired
    private RefundinfoMapper refundinfoMapper;

    @Override
    public StoreOrderRefundinfo findByOrderNo(String orderNo) {
        return refundinfoMapper.findByOrderNo(orderNo);
    }
}
