package com.ifans.rank.controller;


import com.ifans.api.rank.domain.UserGoodsBagTurnover;
import com.ifans.api.rank.vo.UserGoodsBagVo;
import com.ifans.common.core.annotation.InnerAuth;
import com.ifans.common.core.domain.R;
import com.ifans.common.core.utils.SecurityUtils;
import com.ifans.common.core.web.domain.AjaxResult;
import com.ifans.rank.service.UserGoodsBagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/userGoods")
public class UserGoodsBagController {

    @Autowired
    private UserGoodsBagService userGoodsBagService;

    /**
     * 获取当前用户道具
     */
    @GetMapping("/bag")
    public R bag() {
        List<UserGoodsBagVo> list = userGoodsBagService.selectByUserId(SecurityUtils.getUserId());
        return R.ok(list);
    }

    /**
     * 添加当前用户道具
     */
    /*@InnerAuth
    @PostMapping("/add")
    public R add(@RequestBody UserGoodsBagTurnover turnover) {
        try {
            userGoodsBagService.addGoodsForUser(turnover);
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail();
        }
    }*/

    /**
     * 获取当前用户某个道具的库存数量
     */
    @InnerAuth
    @PostMapping("/total/{goodsId}")
    public AjaxResult total(@PathVariable String goodsId) {
        //return userGoodsBagService.g
        return null;
    }
}
