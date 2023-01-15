package com.ifans.activiti.controller;

import com.ifans.api.activiti.domain.RefundVariables;
import com.ifans.common.core.util.R;
import com.ifans.common.core.web.controller.BaseController;
import com.ifans.common.security.service.IfansUser;
import com.ifans.common.security.util.SecurityUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 退款流程控制层
 */
@RestController
@RequestMapping("/refund")
public class RefundActivitiController extends BaseController {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    /**
     * 根据id查询审核流程
     * 判断流程是否结束+查询历史
     */
    @PostMapping("/processInstanceHasEnd")
    public R processInstanceHasEnd(String instanceId) {
        // 判断流程是否结束，查询正在执行的执行对象表
        ProcessInstance rpi = runtimeService.createProcessInstanceQuery() //创建流程实例查询对象
                .processInstanceId(instanceId)
                .singleResult();

        //说明流程实例结束了
        if (rpi == null) {
            // 查询历史，获取流程的相关信息
            HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()//
                    .processInstanceId(instanceId)//使用流程实例ID查询
                    .singleResult();
            System.out.println(hpi.getId() + "    " + hpi.getStartTime() + "   " + hpi.getEndTime() + "   " + hpi.getDurationInMillis());
        }
        return R.ok();
    }

    /**
     * 启动一个流程实例 只进行流程启动不进入下一步
     */
    @PostMapping("/startProcessAndSetGlobalVariable")
    public R startProcessAndSetGlobalVariable(@Validated RefundVariables refundVariables) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("refundVariables", refundVariables);

            // 启动一个请假流程实例，并绑定businessKey和variables
            ProcessInstance instance = runtimeService.startProcessInstanceByKey("Refund", refundVariables.getRefundApplyId(), variables);

            System.out.println("ProcessDefinitionId:" + instance.getProcessDefinitionId());
            System.out.println("Id:" + instance.getId());
            System.out.println("ActivityId:" + instance.getActivityId());

            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed("启动退款流程实例失败，原因：" + e.getMessage());
        }
    }

    /**
     * 启动一个流程实例 进行流程启动且进入下一步
     */
    @PostMapping("/startProcessAndSetGlobalVariableTo")
    @Transactional
    public R startProcessAndSetGlobalVariableTo(@Validated @RequestBody RefundVariables refundVariables, @RequestParam("candidateGroupMatchStr") String candidateGroupMatchStr) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("refundVariables", refundVariables);
            variables.put("status", 0);

            // 启动一个请假流程实例，并绑定businessKey和variables
            ProcessInstance instance = runtimeService.startProcessInstanceByKey("Refund", refundVariables.getRefundApplyId(), variables);

            System.out.println("ProcessDefinitionId:" + instance.getProcessDefinitionId());
            System.out.println("Id:" + instance.getId());
            System.out.println("ActivityId:" + instance.getActivityId());

            // 执行填写退款申请
            // 根据实例id，获取任务
            Task task = taskService.createTaskQuery()
                    .processInstanceId(instance.getId())
                    .taskCandidateGroup(candidateGroupMatchStr)
                    .singleResult();

            // 进入下一步
            taskService.claim(task.getId(), refundVariables.getUserId());
            taskService.complete(task.getId());
            return R.ok(instance.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed("启动退款流程实例失败，原因：" + e.getMessage());
        }
    }
}
