package com.ifans.activititest;

import com.ifans.activiti.IfansActivitiApplication;
import com.ifans.activiti.utils.SecurityUtil;
import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.ClaimTaskPayloadBuilder;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.model.payloads.ClaimTaskPayload;
import org.activiti.api.task.runtime.TaskRuntime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IfansActivitiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestAct {

    private static final Logger log = LoggerFactory.getLogger(TestAct.class);

    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ProcessRuntime processRuntime;
    @Autowired
    private TaskRuntime taskRuntime;

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ACTIVITI_USER", "GROUP_activitiTeam"})
    public void processDef() {
        Page<ProcessDefinition> processDefinitionPage = processRuntime.processDefinitions(Pageable.of(0, 20));
        log.info("Definition:" + processDefinitionPage.getTotalItems());

        List<ProcessDefinition> content = processDefinitionPage.getContent();
        content.stream().forEach(c -> {
            log.info("===============");
            log.info("DefinitionPage:" + c);
            log.info("===============");
        });
    }

    /**
     * 启动流程实例
     */
    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ACTIVITI_USER", "GROUP_activitiTeam"})
    public void startProcess() {
        //securityUtil.logInAs("admin");
        ProcessInstance instance = processRuntime.start(ProcessPayloadBuilder
                .start()
                .withProcessDefinitionKey("Refund")
                .build());

        log.info(instance.getId());
        log.info(instance.toString());
    }

    /**
     * 拾取任务，完成任务
     */
    @Test
    //@WithMockUser(username = "zz5533114", authorities = {"ROLE_ACTIVITI_USER", "GROUP_guke"})
    public void claimAndCompleteTaskByGuke() {
        securityUtil.logInAs("zz5533114");
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 20));
        if (tasks.getTotalItems() > 0) {
            tasks.getContent().stream().forEach(task -> {
                taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
                log.info("zz5533114 claim task:" + task.getId());

                // 完成任务
                taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).build());
            });
        }
    }

    /**
     * 拾取任务，完成任务
     */
    @Test
    public void claimAndCompleteTaskByAdmin() {
        securityUtil.logInAs("jason");
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 20));
        if (tasks.getTotalItems() > 0) {
            tasks.getContent().stream().forEach(task -> {
                taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
                log.info("jason claim task:" + task.getId());

                // 完成任务
                taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).build());
            });
        }
    }
}
