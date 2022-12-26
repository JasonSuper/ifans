package com.ifans.activititest;

import com.ifans.activiti.IfansActivitiApplication;
import com.ifans.activiti.model.Qj;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IfansActivitiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestActivitiUEL {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    /**
     * 创建请假流程定义
     */
    @Test
    public void createQj() {
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("bpmn/Qj.bpmn20.xml")
                .addClasspathResource("bpmn/Qj.png")
                .name("请假流程")
                .deploy();

        System.out.println("部署id=" + deploy.getId());
        System.out.println("部署Name=" + deploy.getName());
    }

    /**
     * 启动一个请假流程实例
     * 并设置Global流程变量
     */
    @Test
    public void startProcessAndSerGlobalVariable() {
        // 初始化请假表单参数，模拟前端传过来的表单参数
        Qj qj = new Qj();
        qj.setId(UUID.randomUUID().toString().replace("-", ""));
        qj.setUaerId("1");
        qj.setUaerName("东方不败");
        qj.setBeginDate(new Date());
        qj.setEndData(new Date(new Date().getTime() + 10000000));
        qj.setQjName("请假单");
        qj.setNum(3d);
        qj.setReson("dia人");

        // 这里的assignee，可以在前端通过参数传到这里
        Map<String, Object> variables = new HashMap<>();
        variables.put("assignee0", "Jack");
        variables.put("assignee1", "Rose");
        variables.put("assignee2", "Anna");
        variables.put("assignee3", "Lara");
        variables.put("qj", qj);

        // 启动一个请假流程实例
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("Qj", variables);

        // 设置流程实例全局变量
        //runtimeService.setVariable(instance.getId(), "qj", qj);

        System.out.println("ProcessDefinitionId:" + instance.getProcessDefinitionId());
        System.out.println("Id:" + instance.getId());
        System.out.println("ActivityId:" + instance.getActivityId());
    }

    /**
     * 根据流程实例id设置全局变量，流程实例必须未执行完成
     */
    @Test
    public void setGlobalVariableByExecutionId() {
        // 模拟前端传过来的参数
        Qj qj = new Qj();
        qj.setNum(5d);

        // 查询当前流程实例id，可通过前端传递
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processDefinitionId("Qj:1:0e28848c-8062-11ed-8f0c-001a7dda7111")
                .singleResult();

        // 通过流程实例id，设置流程变量
        // 注意会覆盖原有key的所有参数，并不是增量修改
        runtimeService.setVariable(instance.getId(), "qj", qj);
    }

    /**
     * 根据任务id设置全局变量
     * 任务必须是当前代办任务id，act_ru_task中存在，如果任务结束，会报错
     */
    @Test
    public void setGlobalVariableByTaskId() {
        // 模拟前端传过来的参数
        Qj qj = new Qj();
        qj.setNum(5d);

        // 查询当前任务id，可通过前端传递
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("Qj")
                .taskAssignee("Rose")
                .singleResult();

        // 通过任务id，设置流程变量
        // 注意会覆盖原有key的所有参数，并不是增量修改
        taskService.setVariable(task.getId(), "qj", qj);
    }

    /**
     * 完成任务
     */
    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ACTIVITI_ADMIN"})
    public void completTask() {
        // 查询任务，返回任务对象
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("Qj")
                .taskAssignee("Lara")
                .singleResult();

        // 模拟前端传过来的参数
        Qj qj = new Qj();
        qj.setNum(5d);

        // 可以在前端通过参数传到这里
        Map<String, Object> variables = new HashMap<>();
        variables.put("qj", qj);

        // 完成任务，参数：任务id
        //taskService.complete(task.getId());
        taskService.complete(task.getId(), variables);
    }

    /**
     * 根据任务id设置local变量
     * 任务办理时设置local流程变量，当前运行的流程实例local变量只能在该任务结束前使用，任务结束该变量无法在当前流程实例使用
     */
    @Test
    public void setLocalVariableByTaksId() {
        // 查询任务，返回任务对象
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("Qj")
                .taskAssignee("Lara")
                .singleResult();

        // 模拟前端传过来的参数
        Qj qj = new Qj();
        qj.setNum(5d);

        // 可以在前端通过参数传到这里
        Map<String, Object> variables = new HashMap<>();
        variables.put("qj", qj);

        // 完成任务，参数：任务id
        //taskService.complete(task.getId());
        taskService.setVariableLocal(task.getId(), "qj", qj);
    }
}
