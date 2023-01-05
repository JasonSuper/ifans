package com.ifans.store.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifans.api.store.domain.StoreGoods;
import com.ifans.common.core.util.R;
import com.ifans.store.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/goods")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @PostMapping("/list")
    public R list(@RequestBody Page page) {
        IPage<StoreGoods> list = storeService.pageList(page, 0);
        return R.ok(list);
    }

    @GetMapping("/info/{id}")
    public R info(@PathVariable String id) {
        StoreGoods storeGoods = storeService.getById(id);
        return R.ok(JSON.toJSONString(storeGoods), "成功");
    }
}
