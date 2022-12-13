package com.ifans.system.controller;

import com.ifans.api.system.domain.UserGoodsBag;
import com.ifans.common.core.annotation.InnerAuth;
import com.ifans.common.core.domain.R;
import com.ifans.system.service.UserGoodsBagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userGoods")
public class UserGoodsBagController {

    @Autowired
    private UserGoodsBagService userGoodsBagService;

    /**
     * 获取当前用户道具
     */
    @InnerAuth
    @GetMapping("/bag/{userId}")
    public R<UserGoodsBag> bag(@PathVariable("userId") String userId) {
        UserGoodsBag userGoodsBag = userGoodsBagService.selectByUserId(userId);
        return R.ok(userGoodsBag);
    }

    /**
     * 添加当前用户道具
     */
    @InnerAuth
    @PostMapping("/add")
    public R add(@RequestBody UserGoodsBag userGoodsBag) {
        boolean isOk = userGoodsBagService.save(userGoodsBag);
        if(isOk) {
            return R.ok();
        } else {
            return R.fail();
        }
    }
}
