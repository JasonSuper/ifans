package com.ifans.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ifans.api.system.domain.UserGoodsBag;

public interface UserGoodsBagMapper extends BaseMapper<UserGoodsBag> {

    UserGoodsBag selectByUserId(String userid);
}
