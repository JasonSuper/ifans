package com.ifans.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ifans.api.activiti.domain.ActBusRelation;

public interface ActBusRelationService extends IService<ActBusRelation> {

    int updateBusBpmStatus(String processInstanceId, int bpmStatus, String businessKey);
}
