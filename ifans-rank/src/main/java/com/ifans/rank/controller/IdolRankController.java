package com.ifans.rank.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifans.api.rank.domain.UserGoodsBag;
import com.ifans.common.core.domain.R;
import com.ifans.common.core.utils.SecurityUtils;
import com.ifans.common.core.web.domain.AjaxResult;
import com.ifans.rank.domain.IdolRank;
import com.ifans.rank.mapper.UserGoodsBagMapper;
import com.ifans.rank.service.IdolRankService;
import com.ifans.rank.service.UserGoodsBagService;
import com.ifans.rank.vo.HitCallVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class IdolRankController {

    @Autowired
    private IdolRankService idolRankService;
    @Autowired
    private UserGoodsBagService userGoodsBagService;

    @PostMapping("/list")
    public AjaxResult list(@RequestBody Page page) {
        IPage<IdolRank> list = idolRankService.pageList(page, 0);
        return AjaxResult.success(list);
    }

    /**
     * 打call
     */
    @PostMapping("/hitCall")
    public R hitCall(@RequestBody HitCallVo hitCallVo) {
        hitCallVo.setUserId(SecurityUtils.getUserId());
        Map<String, Object> result = new HashMap<>();

        LambdaQueryWrapper<UserGoodsBag> query = new LambdaQueryWrapper<>();
        query.eq(UserGoodsBag::getUserId, SecurityUtils.getUserId())
                .eq(UserGoodsBag::getGoodsId, hitCallVo.getGoodsId())
                .ge(UserGoodsBag::getTotal, hitCallVo.getNums());
        UserGoodsBag userGoodsBag = userGoodsBagService.getOne(query);

        if (userGoodsBag == null) {
            result.put("code", "500");
            result.put("msg", "打Call失败，您的道具数量不足！");
            return R.ok(result);
        } else {
            // 填充cas参数
            hitCallVo.setOldTotal(userGoodsBag.getTotal());
            hitCallVo.setNewTotal(userGoodsBag.getTotal() - hitCallVo.getNums());
            hitCallVo.setVersion(userGoodsBag.getVersion());

            // 扣减用户库存
            int i = idolRankService.hitCall(hitCallVo);

            if (i > 0) {
                result.put("code", "200");
                result.put("rankHot", idolRankService.giveMeGoodsRankHot(hitCallVo.getGoodsId()));
                result.put("msg", "打Call成功");
            } else {
                result.put("code", "500");
                result.put("msg", "打Call失败！");
            }
            return R.ok(result);
        }
    }

    @GetMapping("/test")
    public int testcache() {
        return idolRankService.giveMeGoodsRankHot("1");
    }
}
