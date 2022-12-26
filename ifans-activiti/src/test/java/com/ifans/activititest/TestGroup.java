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
public class TestGroup {

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
                .addClasspathResource("bpmn/Qj-Group.bpmn20.xml")
                .addClasspathResource("bpmn/Qj-Group.png")
                .name("请假流程-group")
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
        variables.put("assignee0", "Jack");
        variables.put("qj", qj);

        // 启动一个请假流程实例
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("QjGroup", variables);

        // 设置流程实例全局变量
        //runtimeService.setVariable(instance.getId(), "qj", qj);

        System.out.println("ProcessDefinitionId:" + instance.getProcessDefinitionId());
        System.out.println("Id:" + instance.getId());
        System.out.println("ActivityId:" + instance.getActivityId());
    }

    /**
     * 查询组任务
     * 根据候选人查询组任务
     */
    @Test
    public void findGroupTaskList() {
        // 查询任务，返回任务对象
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("QjGroup")
                .taskCandidateGroup("Lara") // 根据候选人查询
                .list();

        list.stream().forEach(task -> {
            System.out.println("流程实例id:" + task.getProcessInstanceId());
            System.out.println("任务id:" + task.getId());
            System.out.println("候选人:" + task.getAssignee());
            System.out.println("任务名:" + task.getName());
        });
    }

    /**
     * 拾取任务
     * 即使用户不是任务的候选人，也能拾取任务，建议拾取是校验是否有资格
     * 组任务拾取后，该任务已有负责人，通过候选人将查询不到该任务
     */
    @Test
    public void claimTask() {
        // 前端传递的参数
        String taskId = "";
        String userId = "Lara"; // 拾取任务的负责人id

        Task task = taskService.createTaskQuery()
                .processDefinitionKey("QjGroup")
                //.taskId(taskId)
                .taskCandidateGroup(userId)
                .singleResult();

        // 权限验证
        if (task != null) {
            // 设置任务的负责人
            taskService.claim(task.getId(), userId);
        }
    }

    /**
     * 归还组任务
     * 将个人任务变为组任务，还可以进行任务交接
     */
    @Test
    public void setAssigneeToGroupTask() {
        // 前端传递的参数
        String taskId = "";
        String userId = "Lara"; // 拾取任务的负责人id

        Task task = taskService.createTaskQuery()
                .processDefinitionKey("QjGroup")
                .taskAssignee(userId)
                .singleResult();

        // 权限验证
        if (task != null) {
            // 将负责人置空就可以归还组任务
            taskService.setAssignee(task.getId(), null);
        }
    }

    /**
     * 完成任务
     */
    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ACTIVITI_ADMIN"})
    public void completTask() {
        // 查询任务，返回任务对象
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("QjGroup")
                .taskAssignee("Lara")
                .singleResult();

        // 完成任务，参数：任务id
        taskService.complete(task.getId());
    }
}
