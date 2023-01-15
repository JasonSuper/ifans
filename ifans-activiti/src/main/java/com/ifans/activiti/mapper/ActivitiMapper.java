package com.ifans.activiti.mapper;

import java.util.List;
import java.util.Map;

public interface ActivitiMapper {

    List<Map<String, Object>> auditHis(String instanceId);
}
