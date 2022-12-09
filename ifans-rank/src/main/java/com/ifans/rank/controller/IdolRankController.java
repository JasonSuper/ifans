package com.ifans.rank.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifans.common.core.web.domain.AjaxResult;
import com.ifans.rank.domain.IdolRank;
import com.ifans.rank.service.IdolRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IdolRankController {

    @Autowired
    private IdolRankService idolRankService;

    @PostMapping("/list")
    public AjaxResult list(@RequestBody Page page) {
        IPage<IdolRank> list = idolRankService.pageList(page, 0);
        return AjaxResult.success(list);
    }
}
