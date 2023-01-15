package com.ifans.activiti.controller;

import com.ifans.activiti.model.ProcessBpmnEntity;
import com.ifans.activiti.service.ActivitiService;
import com.ifans.common.core.util.R;
import com.ifans.common.core.web.controller.BaseController;
import com.ifans.common.core.web.page.PageDomain;
import com.ifans.common.core.web.page.TableDataInfo;
import com.ifans.common.core.web.page.TableSupport;
import com.ifans.common.security.util.SecurityUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流控制层
 */
@RestController
public class ActivitiController extends BaseController {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ActivitiService activitiService;

    /**
     * 我的任务
     * 查询当前登录用户，需要处理的任务
     */
    @GetMapping("/myTask/list")
    public TableDataInfo myTask() {
        // 在request获取分页参数
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();

        // 查询适合当前用户的任务
        List<Map<String, Object>> list = activitiService.myTaskList((pageNum - 1) * pageSize, pageSize);
        // 总条数
        long total = activitiService.myTaskTotal();
        // 封装返回数据
        TableDataInfo result = new TableDataInfo(list, (int) total);
        return result;

//        startPage();
//        // 查询适合当前用户的任务
//        List<ActBusRelation> list = activitiService.actBusRelationList();
//        return getDataTable(list);
    }

    /**
     * 我发起的流程
     * 查询当前登录用户发起的流程
     */
    @GetMapping("/myProcess/list")
    public R myProcess() {
        return R.ok();
    }

    /**
     * 拾取任务
     *
     * @param taskId 任务id
     */
    @PostMapping("/claimTask")
    public R claimTask(String taskId) {
        try {
            // 根据实例id，获取任务
            Task task = taskService.createTaskQuery()
                    .taskId(taskId)
                    .taskCandidateGroupIn(activitiService.getMyAuthRole())
                    .singleResult();

            // 权限验证
            if (task != null) {
                // 设置任务的负责人
                taskService.claim(task.getId(), SecurityUtils.getUser().getId());
                return R.ok();
            } else {
                return R.failed("你没有权限拾取该任务");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed("拾取任务失败，原因：" + e.getMessage());
        }
    }

    /**
     * 完成任务
     */
    @PostMapping("/completTask")
    public R completTask(String taskId, int bpmStatus, String reason) {
        try {
            R r = activitiService.completTask(taskId, bpmStatus, reason);
            return r;
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed("完成任务失败，原因：" + e.getMessage());
        }
    }

    /**
     * 根据实例id查询审核流程历史
     */
    @GetMapping("/auditHis/{instanceId}")
    public R auditHis(@PathVariable("instanceId") String instanceId) {
        try {
            List<Map<String, Object>> resList = new ArrayList<>();

            // 查询历史，获取流程的相关信息
            /*List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery() //创建查询对象
                    .processInstanceId(instanceId) //使用流程实例ID查询
                    .list();*/

            List<Map<String, Object>> list = activitiService.auditHis(instanceId);
            list.stream().forEach(hiTask -> {
                Map<String, Object> res = new HashMap<>();
                res.put("name", hiTask.get("NAME_"));
                res.put("assignee", hiTask.get("ASSIGNEE_"));
                res.put("assigneeName", hiTask.get("assigneeName"));
                res.put("startTime", hiTask.get("START_TIME_"));
                res.put("endTime", hiTask.get("END_TIME_"));
                //res.put("reason", "");
                List<Comment> taskComments = taskService.getTaskComments(hiTask.get("ID_").toString());
                if (taskComments != null && taskComments.size() > 0) {
                    res.put("reason", taskComments.get(0).getFullMessage());
                }
                resList.add(res);
            });
            return R.ok(resList);
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed();
        }
    }

    /**
     * 审核流程图进度展示
     *
     * @param instanceId 流程实例id
     * @param isFinish   流程实例是否已结束
     */
    @GetMapping("/processImg")
    public void processImg(@RequestParam("instanceId") String instanceId,
                           @RequestParam("isFinish") boolean isFinish,
                           HttpServletResponse response) throws IOException {
        // 判断流程是否正在进行中，如果正在进行中，需要高亮节点，如果已经结束，只需要看流程图，不需要高亮节点
        InputStream is = activitiService.processImg(instanceId, isFinish);
        IOUtils.copy(is, response.getOutputStream());
    }

    /**
     * 审核流程图进度展示 - 进阶
     * 返回流程定义xml，前端利用bpmn.js展示，更加美观和灵活
     *
     * @param instanceId 流程实例id
     */
    @GetMapping("/processBpmn")
    public R processBpmn(@RequestParam("instanceId") String instanceId) throws Exception {
        // 获取流程定义数据
        ProcessBpmnEntity bpmnEntity = activitiService.initProcessBpmn(instanceId);
        return R.ok(bpmnEntity);
    }
}
