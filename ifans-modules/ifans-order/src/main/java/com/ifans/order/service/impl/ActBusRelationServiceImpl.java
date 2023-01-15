package com.ifans.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifans.api.activiti.domain.ActBusRelation;
import com.ifans.api.activiti.vo.ActBusRelationVo;
import com.ifans.order.mapper.ActBusRelationMapper;
import com.ifans.order.service.ActBusRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActBusRelationServiceImpl extends ServiceImpl<ActBusRelationMapper, ActBusRelation> implements ActBusRelationService {

    @Autowired
    private ActBusRelationMapper actBusRelationMapper;

    @Override
    public int updateBusBpmStatus(String processInstanceId, int bpmStatus, String businessKey) {
        ActBusRelationVo one = actBusRelationMapper.findByInstanceId(processInstanceId);
        return actBusRelationMapper.doUpdateBusBpmStatus("ifans." + one.getBusTableName(), bpmStatus, businessKey);
    }
}
