package com.ifans.activiti.service;

import com.ifans.activiti.model.ProcessBpmnEntity;
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
}
