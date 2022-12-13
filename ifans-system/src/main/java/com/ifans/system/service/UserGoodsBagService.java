package com.ifans.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ifans.api.system.domain.UserGoodsBag;

public interface UserGoodsBagService extends IService<UserGoodsBag> {

    UserGoodsBag selectByUserId(String userid);
}
