package com.ifans.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifans.api.system.domain.UserGoodsBag;
import com.ifans.system.mapper.UserGoodsBagMapper;
import com.ifans.system.service.UserGoodsBagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserGoodsBagServiceImpl extends ServiceImpl<UserGoodsBagMapper, UserGoodsBag> implements UserGoodsBagService {

    @Autowired
    private UserGoodsBagMapper userGoodsBagMapper;

    @Override
    public UserGoodsBag selectByUserId(String userid) {
        return userGoodsBagMapper.selectById(userid);
    }
}
