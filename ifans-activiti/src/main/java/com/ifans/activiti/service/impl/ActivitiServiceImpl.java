package com.ifans.activiti.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.ifans.activiti.model.ProcessBpmnEntity;
import com.ifans.activiti.service.ActivitiService;
import com.ifans.api.activiti.domain.RefundVariables;
import com.ifans.common.redis.service.RedisService;
import com.ifans.common.security.util.SecurityUtils;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ActivitiServiceImpl implements ActivitiService {

    private Logger log = LoggerFactory.getLogger(ActivitiServiceImpl.class);

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 审核流程图xml数据的缓存
     */
    private static final String PROCESS_XML_CACHE = "process-xml-cache:";
    private static final String PROCESS_XML_CACHE_LOCK = "process-xml-cache-lock:";

    /**
     * 自定义sql查询当前用户的任务列表
     */
    @Override
    public List<Map<String, Object>> myTaskList(int firstResult, int maxResults) {
        List<Task> list = taskService.createNativeTaskQuery().sql("SELECT art.* FROM act_ru_task art "
                        + "left join act_ru_identitylink ari on ari.TASK_ID_ = art.ID_ "
                        + "where ari.GROUP_ID_ in (" + getMyAuthRoleToStr() + ") "
                        + "or art.ASSIGNEE_ = " + "'" + SecurityUtils.getUser().getId() + "' ")
                .listPage(firstResult, maxResults);

        // 封装前端需要显示的数据
        List<Map<String, Object>> resList = list.stream().map(task -> {
            // 查询流程实例信息
            ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            // 查询全局参数
            Map<String, Object> variables = runtimeService.getVariables(task.getExecutionId());

            Map<String, Object> map = new HashMap<>();
            //map.put("title", );
            map.put("processInstanceId", task.getProcessInstanceId());
            map.put("taskId", task.getId());
            map.put("instanceName", instance.getProcessDefinitionName());
            map.put("processInstanceId", task.getProcessInstanceId());
            map.put("applyUser", ((RefundVariables) variables.get("refundVariables")).getUserName());
            map.put("startTime", task.getCreateTime());
            map.put("taskName", task.getName());
            map.put("isAssignee", SecurityUtils.getUser().getId().equals(task.getAssignee()));
            return map;
        }).collect(Collectors.toList());
        return resList;
    }

    /**
     * 自定义sql查询当前用户的任务列表总数
     */
    @Override
    public long myTaskTotal() {
        long count = taskService.createNativeTaskQuery().sql("SELECT count(*) FROM act_ru_task art "
                        + "left join act_ru_identitylink ari on ari.TASK_ID_ = art.ID_ "
                        + "where ari.GROUP_ID_ in (" + getMyAuthRoleToStr() + ") "
                        + "or art.ASSIGNEE_ = " + "'" + SecurityUtils.getUser().getId() + "' ")
                .count();
        return count;
    }

    @Override
    public List<String> getMyAuthRole() {
        // 获取当前用户的角色
        List<String> authorities = SecurityUtils.getUser().getAuthorities().stream()
                .filter(auth -> auth.getAuthority().indexOf("ROLE_") == 0)
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList());
        return authorities;
    }

    /**
     * 获取当前用户的所有角色
     */
    private String getMyAuthRoleToStr() {
        // 获取当前用户的角色
        List<String> authorities = SecurityUtils.getUser().getAuthorities().stream()
                .filter(auth -> auth.getAuthority().indexOf("ROLE_") == 0)
                .map(auth -> "'" + auth.getAuthority().replace("ROLE_", "") + "'")
                .collect(Collectors.toList());
        return String.join(",", authorities);
    }

    /**
     * 审核流程图进度展示
     *
     * @param instanceId 流程实例id
     * @param isFinish   流程实例是否已结束
     */
    @Override
    public InputStream processImg(String instanceId, boolean isFinish) {
        ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
        // 判断流程是否正在进行中，如果正在进行中，需要高亮节点，如果已经结束，只需要看流程图，不需要高亮节点
        BpmnModel bpmnModel = repositoryService.getBpmnModel(instance.getProcessDefinitionId());
        List<String> highLightedActivities = null;
        if (true) {
            // 流程正在流转，查找当前节点，高亮当前节点
            highLightedActivities = runtimeService.getActiveActivityIds(instanceId);
        } else {
            // 流程已经结束，不需要高亮节点
            highLightedActivities = Collections.EMPTY_LIST;
        }
        // 获取png文件
        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
        // 生成输入流
        InputStream inputStream = generator.generateDiagram(bpmnModel, highLightedActivities, Collections.EMPTY_LIST, "宋体", "宋体", "宋体");
        return inputStream;
    }

    /**
     * 审核流程图进度展示 - 进阶
     * 返回流程定义xml，前端利用bpmn.js展示，更加美观和灵活
     *
     * @param instanceId 流程实例id
     */
    @Override
    public ProcessBpmnEntity initProcessBpmn(String instanceId) throws Exception {
        ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(instance.getProcessDefinitionId());
        BpmnModel bpmnModel = repositoryService.getBpmnModel(instance.getProcessDefinitionId());

        // 获取流程实例执行历史
        List<HistoricActivityInstance> histActInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(instanceId).orderByHistoricActivityInstanceId().asc().list();

        // 构建前端渲染bpmnjs需要的数据
        ProcessBpmnEntity bpmnEntity = getProcessBpmnData(bpmnModel.getMainProcess(), histActInstances);
        bpmnEntity.setModelName(processDefinition.getName());

        String cacheKey = PROCESS_XML_CACHE + processDefinition.getId();
        String cacheLockKey = PROCESS_XML_CACHE_LOCK + processDefinition.getId();
        String cacheXml = redisService.getCacheObject(cacheKey);
        // Map缓存，提高获取流程文件速度
        if (!StrUtil.isEmpty(cacheXml)) {
            bpmnEntity.setModelXml(cacheXml);
        } else {
            // 分布式锁，用于安全的并发构建缓存
            RLock rLock = redissonClient.getLock(cacheLockKey);
            rLock.lock(20, TimeUnit.SECONDS);
            try {
                if (StrUtil.isEmpty((cacheXml = redisService.getCacheObject(cacheKey)))) {
                    InputStream bpmnStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getResourceName());
                    try (Reader reader = new InputStreamReader(bpmnStream, StandardCharsets.UTF_8)) {
                        String xmlString = IoUtil.read(reader);
                        bpmnEntity.setModelXml(xmlString);
                        redisService.setCacheObject(cacheKey, xmlString, 10l, TimeUnit.MINUTES);
                    } catch (IOException e) {
                        log.error("[获取流程数据] 失败，{}", e.getMessage());
                        throw new Exception("获取流程数据失败，请稍后重试");
                    }
                } else {
                    bpmnEntity.setModelXml(cacheXml);
                }
            } finally {
                rLock.unlock();
            }
        }
        return bpmnEntity;
    }

    private ProcessBpmnEntity getProcessBpmnData(Process process, List<HistoricActivityInstance> historicActInstances) {
        ProcessBpmnEntity entity = new ProcessBpmnEntity();
        // 已执行的节点id
        Set<String> executedActivityIds = new HashSet<>();
        // 正在执行的节点id
        Set<String> activeActivityIds = new HashSet<>();
        // 高亮流程已发生流转的线id集合
        Set<String> highLightedFlowIds = new HashSet<>();
        // 全部活动节点
        List<FlowNode> historicActivityNodes = new ArrayList<>();
        // 已完成的历史活动节点
        List<HistoricActivityInstance> finishedActivityInstances = new ArrayList<>();

        // 遍历流程实例执行历史
        for (HistoricActivityInstance historicActivityInstance : historicActInstances) {
            FlowNode flowNode = (FlowNode) process.getFlowElement(historicActivityInstance.getActivityId(), true);
            historicActivityNodes.add(flowNode); // 生成活动节点
            if (historicActivityInstance.getEndTime() != null) {
                // 如果执行历史存在结束时间，表示已完成
                finishedActivityInstances.add(historicActivityInstance); // 生成已完成的历史活动节点
                executedActivityIds.add(historicActivityInstance.getActivityId()); // 记录已执行的节点id
            } else {
                activeActivityIds.add(historicActivityInstance.getActivityId()); // 记录正在执行的节点id
            }
        }

        FlowNode currentFlowNode = null;
        FlowNode targetFlowNode = null;
        // 遍历已完成的活动实例，从每个实例的outgoingFlows中找到已执行的
        for (HistoricActivityInstance currentActivityInstance : finishedActivityInstances) {
            // 获得当前活动对应的节点信息及outgoingFlows信息
            currentFlowNode = (FlowNode) process.getFlowElement(currentActivityInstance.getActivityId(), true);
            List<SequenceFlow> sequenceFlows = currentFlowNode.getOutgoingFlows();

            /**
             * 遍历outgoingFlows并找到已已流转的 满足如下条件认为已已流转：
             * 1.当前节点是并行网关或兼容网关，则通过outgoingFlows能够在历史活动中找到的全部节点均为已流转
             * 2.当前节点是以上两种类型之外的，通过outgoingFlows查找到的时间最早的流转节点视为有效流转
             */
            if ("parallelGateway".equals(currentActivityInstance.getActivityType()) || "inclusiveGateway".equals(currentActivityInstance.getActivityType())) {
                // 遍历历史活动节点，找到匹配流程目标节点的
                for (SequenceFlow sequenceFlow : sequenceFlows) {
                    targetFlowNode = (FlowNode) process.getFlowElement(sequenceFlow.getTargetRef(), true);
                    if (historicActivityNodes.contains(targetFlowNode)) {
                        highLightedFlowIds.add(sequenceFlow.getId());
                    }
                }
            } else {
                List<Map<String, Object>> tempMapList = new ArrayList<>();
                for (SequenceFlow sequenceFlow : sequenceFlows) {
                    for (HistoricActivityInstance historicActivityInstance : historicActInstances) {
                        if (historicActivityInstance.getActivityId().equals(sequenceFlow.getTargetRef())) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("highLightedFlowId", sequenceFlow.getId());
                            map.put("highLightedFlowStartTime", historicActivityInstance.getStartTime().getTime());
                            tempMapList.add(map);
                        }
                    }
                }

                if (!CollectionUtils.isEmpty(tempMapList)) {
                    // 遍历匹配的集合，取得开始时间最早的一个
                    long earliestStamp = 0L;
                    String highLightedFlowId = null;
                    for (Map<String, Object> map : tempMapList) {
                        long highLightedFlowStartTime = Long.parseLong(map.get("highLightedFlowStartTime").toString());
                        if (earliestStamp == 0 || earliestStamp >= highLightedFlowStartTime) {
                            highLightedFlowId = map.get("highLightedFlowId").toString();
                            earliestStamp = highLightedFlowStartTime;
                        }
                    }
                    highLightedFlowIds.add(highLightedFlowId);
                }
            }
        }

        entity.setActiveActivityIds(activeActivityIds);
        entity.setExecutedActivityIds(executedActivityIds);
        entity.setHighlightedFlowIds(highLightedFlowIds);
        return entity;
    }
}
