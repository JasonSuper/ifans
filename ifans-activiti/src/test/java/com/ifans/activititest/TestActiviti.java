package com.ifans.activititest;

import com.ifans.activiti.IfansActivitiApplication;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.List;
import java.util.zip.ZipInputStream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IfansActivitiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestActiviti {

    /*@Autowired
    private WebApplicationContext context;*/
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    /*private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                // 添加spring-security的验证
                //.apply(springSecurity())
                .build();
    }*/

    /**
     * 创建流程
     */
    @Test
    public void create() {
        /*ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();*/

        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/Sh.bpmn20.xml")
                .addClasspathResource("bpmn/Sh.png")
                //.addInputStream("", null)
                .name("审核流程")
                .deploy();

        System.out.println("部署id" + deployment.getId());
        System.out.println("部署Name" + deployment.getName());
    }

    /**
     * 创建流程
     * zip压缩包，批量创建流程
     */
    @Test
    public void createByZip() {
        // 读取压缩包
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("bpmn/bpmn.zip");
        ZipInputStream zipInputStream = new ZipInputStream(in);

        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();

        System.out.println("id" + deployment.getId());
        System.out.println("Name" + deployment.getName());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void startProcess() {
        // 启动实例
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("ShTest1");
        System.out.println("ProcessDefinitionId:" + instance.getProcessDefinitionId());
        System.out.println("Id:" + instance.getId());
        System.out.println("ActivityId:" + instance.getActivityId());
    }

    /**
     * 任务查询
     */
    @Test
    public void findProcessInstance() {
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("ShTest1")
                .taskAssignee("Jack")
                .list();

        list.stream().forEach(task -> {
            System.out.println("流程定义id:" + task.getProcessDefinitionId());
            System.out.println("任务id:" + task.getId());
            System.out.println("任务的命名:" + task.getAssignee());
            System.out.println("任务名:" + task.getName());
        });
    }

    /**
     * 完成任务
     */
    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ACTIVITI_ADMIN"})
    public void completTask() {
        // 查询任务，返回任务对象
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("ShTest1")
                .taskAssignee("Rose")
                .singleResult();

        // 完成任务，参数：任务id
        taskService.complete(task.getId());
    }

    /**
     * 流程定义信息查询
     */
    @Test
    public void queryProcessDefinition() {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("ShTest1")
                .orderByProcessDefinitionVersion().desc()
                .list();

        list.stream().forEach(def -> {
            System.out.println("id=" + def.getId());
            System.out.println("name=" + def.getName());
            System.out.println("key=" + def.getKey());
            System.out.println("version=" + def.getVersion());
            System.out.println("deploymentId=" + def.getDeploymentId());
        });
    }

    /**
     * 删除流程
     */
    @Test
    public void deleteDepleyment() {
        // 流程执行中是无法删除流程的，通过第二个参数cascade参数决定是否级联删除
        repositoryService.deleteDeployment("e45e384f-7fa3-11ed-b6bd-001a7dda7111", false);
    }

    /**
     * 流程资源下载
     */
    @Test
    public void downloadFile() throws IOException {
        // 获取相应流程定义数据
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("ShTest1").singleResult();

        InputStream inputStream = null;
        File file = new File("C:/Users/hjxsu/Desktop/" + definition.getResourceName());
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        try {
            // 获取资源输入流
            inputStream = repositoryService.getResourceAsStream(definition.getDeploymentId(), definition.getResourceName()); // 获取xml文件
            //inputStream = repositoryService.getResourceAsStream(definition.getDeploymentId(), definition.getDiagramResourceName()); // 获取png文件
            IOUtils.copy(inputStream, fileOutputStream);
        } finally {
            // 关闭流
            if (inputStream != null)
                inputStream.close();

            fileOutputStream.close();
        }
    }

    /**
     * 历史任务查询
     */
    @Test
    public void findHistoryInfo() {
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                .processDefinitionId("ShTest1:1:e4798782-7fa3-11ed-b6bd-001a7dda7111")
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();

        list.stream().forEach(hi -> {
            System.out.println(hi.getActivityId());
            System.out.println(hi.getActivityName());
            System.out.println(hi.getProcessDefinitionId());
            System.out.println(hi.getProcessInstanceId());
            System.out.println("===========================");
        });
    }

    /**
     * 业务使用BusinessKey和activiti流程绑定
     */
    @Test
    public void startProcessWithBusinessKey() {
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("ShTest1", "1");
        System.out.println(instance.getBusinessKey());
    }
}
