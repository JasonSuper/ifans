package com.ifans.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ifans.api.activiti.domain.ActBusRelation;
import com.ifans.api.activiti.vo.ActBusRelationVo;
import org.apache.ibatis.annotations.Param;

public interface ActBusRelationMapper extends BaseMapper<ActBusRelation> {

    int doUpdateBusBpmStatus(@Param("busTableName") String busTableName, @Param("bpmStatus") int bpmStatus, @Param("busId") String busId);

    ActBusRelationVo findByInstanceId(String processInstanceId);
}
