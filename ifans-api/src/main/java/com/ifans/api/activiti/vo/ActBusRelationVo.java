package com.ifans.api.activiti.vo;

import com.ifans.api.activiti.domain.ActBusRelation;
import lombok.Data;

@Data
public class ActBusRelationVo extends ActBusRelation {

    private String activitiInstanceId;
}
