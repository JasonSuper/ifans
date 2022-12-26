package com.ifans.activititest;

import com.ifans.activiti.IfansActivitiApplication;
import com.ifans.activiti.model.Qj;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IfansActivitiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestInclusive {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    /**
     * 创建流程定义
     */
    @Test
    public void create() {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/Qj-Inclusive.bpmn20.xml")
                .addClasspathResource("bpmn/Qj-Inclusive.png")
                .name("请假流程-inclusive")
                .deploy();

        System.out.println("部署id" + deployment.getId());
        System.out.println("部署Name" + deployment.getName());
    }

    /**
     * 启动一个请假流程实例
     * 并设置Global流程变量
     */
    @Test
    public void startProcessAndSerGlobalVariable() {
        // 初始化请假表单参数，模拟前端传过来的表单参数
        Qj qj = new Qj();
        qj.setNum(3d);

        // 这里的assignee，可以在前端通过参数传到这里
        Map<String, Object> variables = new HashMap<>();
        variables.put("qj", qj);

        // 启动一个请假流程实例
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("QjInclusive", variables);

        System.out.println("ProcessDefinitionId:" + instance.getProcessDefinitionId());
        System.out.println("Id:" + instance.getId());
        System.out.println("ActivityId:" + instance.getActivityId());
    }

    /**
     * 完成任务
     */
    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ACTIVITI_ADMIN"})
    public void completTask() {
        // 查询任务，返回任务对象
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("QjInclusive")
                .taskAssignee("5")
                .singleResult();

        // 完成任务，参数：任务id
        taskService.complete(task.getId());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ACTIVITI_USER", "GROUP_activitiTeam"})
    public void deleteDeployment() {
        // 流程执行中是无法删除流程的，通过第二个参数cascade参数决定是否级联删除
        repositoryService.deleteDeployment("af59933e-8116-11ed-91ac-001a7dda7111", true);
    }

    /**
     * 判断流程是否结束+查询历史
     */
    @Test
    public void processInstanceHasEnd() {
        // 判断流程是否结束，查询正在执行的执行对象表
        ProcessInstance rpi = runtimeService.createProcessInstanceQuery()//创建流程实例查询对象
                .processInstanceId("740663a1-8117-11ed-b4d2-001a7dda7111")
                .singleResult();

        //说明流程实例结束了
        if (rpi == null) {
            // 查询历史，获取流程的相关信息
            HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()//
                    .processInstanceId("740663a1-8117-11ed-b4d2-001a7dda7111")//使用流程实例ID查询
                    .singleResult();
            System.out.println(hpi.getId() + "    " + hpi.getStartTime() + "   " + hpi.getEndTime() + "   " + hpi.getDurationInMillis());
        }
    }
}
