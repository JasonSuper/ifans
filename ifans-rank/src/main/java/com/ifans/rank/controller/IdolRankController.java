package com.ifans.rank.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifans.api.rank.domain.UserGoodsBag;
import com.ifans.common.core.util.R;
import com.ifans.common.security.util.SecurityUtils;
import com.ifans.rank.domain.IdolRank;
import com.ifans.rank.service.IdolRankService;
import com.ifans.rank.service.UserGoodsBagService;
import com.ifans.rank.vo.HitCallVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class IdolRankController {

    @Autowired
    private IdolRankService idolRankService;
    @Autowired
    private UserGoodsBagService userGoodsBagService;

    @PostMapping("/list")
    public R list(@RequestBody Page page) {
        IPage<IdolRank> list = idolRankService.pageList(page, 0);
        return R.ok(list);
    }

    /**
     * 打call
     */
    @PostMapping("/hitCall")
    public R hitCall(@RequestBody HitCallVo hitCallVo) {
        hitCallVo.setUserId(SecurityUtils.getUser().getId());

        // 查询用户要使用的道具，库存状况
        UserGoodsBag userGoodsBag = userGoodsBagService.searchUserGoods(hitCallVo);

        Map<String, Object> result = new HashMap<>();
        if (userGoodsBag == null || userGoodsBag.getTotal() - hitCallVo.getNums() < 0) {
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
