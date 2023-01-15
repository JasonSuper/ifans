package com.ifans.activiti.service;

import com.ifans.activiti.model.ProcessBpmnEntity;
import com.ifans.api.activiti.domain.ActBusRelation;
import com.ifans.common.core.util.R;
import org.activiti.engine.task.Task;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface ActivitiService {

    List<Map<String, Object>> myTaskList(int firstResult, int maxResults);

    long myTaskTotal();

    List<String> getMyAuthRole();

    InputStream processImg(String instanceId, boolean isFinish);

    ProcessBpmnEntity initProcessBpmn(String instanceId) throws Exception;

    List<ActBusRelation> actBusRelationList();

    List<Map<String, Object>> auditHis(String instanceId);

    R completTask(String taskId, int bpmStatus, String reason);
}
