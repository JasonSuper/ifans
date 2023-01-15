package com.ifans.order.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ifans.api.activiti.FeignActivitiService;
import com.ifans.api.activiti.FeignRefundActivitiService;
import com.ifans.api.activiti.domain.ActBusRelation;
import com.ifans.api.activiti.domain.RefundVariables;
import com.ifans.api.order.domain.ActBusRealtionRefundapply;
import com.ifans.api.order.domain.StoreOrder;
import com.ifans.api.order.domain.StoreOrderRefundapply;
import com.ifans.common.core.util.R;
import com.ifans.common.core.util.poi.ExcelUtil;
import com.ifans.common.core.web.controller.BaseController;
import com.ifans.common.core.web.domain.AjaxResult;
import com.ifans.common.core.web.page.TableDataInfo;
import com.ifans.common.security.util.SecurityUtils;
import com.ifans.order.service.ActBusRealtionRefundapplyService;
import com.ifans.order.service.ActBusRelationService;
import com.ifans.order.service.OrderService;
import com.ifans.order.service.StoreOrderRefundapplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 退款申请
 */
@RestController
@RequestMapping("/refundapply")
public class ReFundApplyController extends BaseController {

    @Autowired
    private StoreOrderRefundapplyService storeOrderRefundapplyService;
    @Autowired
    private FeignActivitiService feignActivitiService;
    @Autowired
    private FeignRefundActivitiService feignRefundActivitiService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ActBusRealtionRefundapplyService actBusRealtionRefundapplyService;
    @Autowired
    private ActBusRelationService actBusRealtionService;

    /**
     * 退款申请列表
     *
     * @param storeOrderRefundapply 查询参数
     * @return
     */
    @PreAuthorize("@pms.hasPermission('order:refundapply:list')")
    @GetMapping("/list")
    public TableDataInfo list(StoreOrderRefundapply storeOrderRefundapply) {
        startPage();
        List<StoreOrderRefundapply> list = storeOrderRefundapplyService.selectStoreOrderRefundapplyList(storeOrderRefundapply);
        return getDataTable(list);
    }

    /**
     * 退款申请详情
     */
    @PreAuthorize("@pms.hasPermission('order:refundapply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable String id) {
        return AjaxResult.success(storeOrderRefundapplyService.selectStoreOrderRefundapplyById(id));
    }

    /**
     * 审批进度查询
     */
    @PreAuthorize("@pms.hasPermission('order:refundapply:progressQuery')")
    @GetMapping("/progressQuery/{refundApplyId}")
    public R progressQuery(@PathVariable("refundApplyId") String refundApplyId) {
        String instanceId = storeOrderRefundapplyService.getInstanceId(refundApplyId);
        // 根据实例id查询审核流程历史
        R r = feignActivitiService.auditHis(instanceId);
        if (r.getCode() == 200) {
            return R.ok(r.getData());
        }
        return R.failed("查询失败！");
    }

    /**
     * 导出退款申请
     */
    @PreAuthorize("@pms.hasPermission('order:refundapply:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, StoreOrderRefundapply storeOrderRefundapply) {
        List<StoreOrderRefundapply> list = storeOrderRefundapplyService.selectStoreOrderRefundapplyList(storeOrderRefundapply);
        ExcelUtil<StoreOrderRefundapply> util = new ExcelUtil<>(StoreOrderRefundapply.class);
        util.exportExcel(response, list, "退款申请");
    }

    /**
     * 新增退款申请
     */
    @PreAuthorize("@pms.hasPermission('order:refundapply:add')")
    @PostMapping
    public R add(@RequestBody StoreOrderRefundapply storeOrderRefundapply) {
        // 构建退款流程参数
        RefundVariables variables = new RefundVariables();
        StoreOrder storeOrder = orderService.getById(storeOrderRefundapply.getOrderId());
        variables.setRefundApplyId(IdWorker.getId() + "");
        variables.setPrice(storeOrder.getPayPrice());
        variables.setUserId(SecurityUtils.getUser().getId());
        variables.setUserName(SecurityUtils.getUser().getUsername());

        // 启动一个退款流程实例
        R r = feignRefundActivitiService.startProcessAndSetGlobalVariableTo(variables, "fans");
        if (r.getCode() == 200) {
            storeOrderRefundapply.setId(variables.getRefundApplyId());
            // 绑定退款实例流程id
            //storeOrderRefundapply.setActivitiInstanceId(r.getData().toString());

            storeOrderRefundapply.setApplyTime(new Date());
            storeOrderRefundapply.setCreateBy(SecurityUtils.getUser().getUsername());
            storeOrderRefundapply.setCreateTime(new Date());
            storeOrderRefundapplyService.save(storeOrderRefundapply);

            // 查询业务关联的对应数据
            ActBusRelation actBusRelation = actBusRealtionService.getOne(Wrappers.<ActBusRelation>lambdaQuery().eq(ActBusRelation::getUniqueCode, "refundapply"));

            // 流程与业务关联
            actBusRealtionRefundapplyService.save(new ActBusRealtionRefundapply(r.getData().toString(), actBusRelation.getId(), storeOrderRefundapply.getId()));
            return R.ok();
        } else {
            return R.failed("退款申请创建失败！");
        }
    }

    /**
     * 修改退款申请
     */
    @PreAuthorize("@pms.hasPermission('order:refundapply:edit')")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody StoreOrderRefundapply storeOrderRefundapply) {
        storeOrderRefundapply.setUpdateBy(SecurityUtils.getUser().getUsername());
        return toAjax(storeOrderRefundapplyService.updateStoreOrderRefundapply(storeOrderRefundapply));
    }

    /**
     * 删除申请
     */
    @PreAuthorize("@pms.hasPermission('order:refundapply:remove')")
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") String id) {
        return toAjax(storeOrderRefundapplyService.deleteStoreOrderRefundapplyById(id));
    }
}
