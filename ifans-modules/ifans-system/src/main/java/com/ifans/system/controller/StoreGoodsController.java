package com.ifans.system.controller;

import com.ifans.api.store.domain.StoreGoods;
import com.ifans.common.core.util.poi.ExcelUtil;
import com.ifans.common.core.web.controller.BaseController;
import com.ifans.common.core.web.domain.AjaxResult;
import com.ifans.common.core.web.page.TableDataInfo;
import com.ifans.common.security.util.SecurityUtils;
import com.ifans.system.service.IStoreGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 道具管理
 */
@RestController
@RequestMapping("/goods")
public class StoreGoodsController extends BaseController {

    @Autowired
    private IStoreGoodsService storeGoodsService;

    @PreAuthorize("@pms.hasPermission('system:goods:list')")
    @GetMapping("/list")
    public TableDataInfo list(StoreGoods storeGoods) {
        startPage();
        List<StoreGoods> list = storeGoodsService.selectStoreGoodsList(storeGoods);
        return getDataTable(list);
    }

    /**
     * 根据商品编号获取详细信息
     */
    @PreAuthorize("@pms.hasPermission('system:goods:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable String id) {
        return AjaxResult.success(storeGoodsService.selectStoreGoodsById(id));
    }

    @PreAuthorize("@pms.hasPermission('system:goods:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, StoreGoods storeGoods) {
        List<StoreGoods> list = storeGoodsService.selectStoreGoodsList(storeGoods);
        ExcelUtil<StoreGoods> util = new ExcelUtil<>(StoreGoods.class);
        util.exportExcel(response, list, "道具信息");
    }

    /**
     * 新增商品
     */
    @PreAuthorize("@pms.hasPermission('system:goods:add')")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody StoreGoods storeGoods) {
        storeGoods.setCreateBy(SecurityUtils.getUser().getUsername());
        return toAjax(storeGoodsService.insertStoreGoods(storeGoods));
    }

    /**
     * 修改商品
     */
    @PreAuthorize("@pms.hasPermission('system:goods:edit')")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody StoreGoods storeGoods) {
        storeGoods.setUpdateBy(SecurityUtils.getUser().getUsername());
        return toAjax(storeGoodsService.updateStoreGoods(storeGoods));
    }

    /**
     * 删除商品
     */
    @PreAuthorize("@pms.hasPermission('system:goods:remove')")
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") String id) {
        return toAjax(storeGoodsService.deleteStoreGoodsById(id));
    }
}
