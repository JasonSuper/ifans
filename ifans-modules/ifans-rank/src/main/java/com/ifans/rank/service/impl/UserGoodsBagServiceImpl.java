package com.ifans.rank.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifans.api.order.domain.StoreOrderItem;
import com.ifans.api.order.vo.StoreOrderVo;
import com.ifans.api.rank.domain.UserGoodsBag;
import com.ifans.api.rank.domain.UserGoodsBagTurnover;
import com.ifans.api.rank.vo.UserGoodsBagVo;
import com.ifans.common.core.util.BeanUtils;
import com.ifans.rank.mapper.UserGoodsBagMapper;
import com.ifans.rank.mapper.UserGoodsBagTurnoverMapper;
import com.ifans.rank.service.UserGoodsBagService;
import com.ifans.rank.vo.HitCallVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class UserGoodsBagServiceImpl extends ServiceImpl<UserGoodsBagMapper, UserGoodsBag> implements UserGoodsBagService {

    @Autowired
    private UserGoodsBagMapper userGoodsBagMapper;
    @Autowired
    private UserGoodsBagTurnoverMapper turnoverMapper;

    @Override
    public List<UserGoodsBagVo> selectByUserId(String userid) {
        return userGoodsBagMapper.selectByUserId(userid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGoodsForUser(UserGoodsBagTurnover turnover) {
        // 添加流水记录
        turnoverMapper.insert(turnover);

        // 拷贝属性
        UserGoodsBag userGoodsBag = new UserGoodsBag();
        BeanUtils.copyBeanProp(userGoodsBag, turnover);

        // 添加用户背包道具
        userGoodsBagMapper.addOrUpdate(userGoodsBag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGoodsForUser(StoreOrderVo storeOrderVo) {
        List<StoreOrderItem> itemList = storeOrderVo.getStoreOrderItems();
        itemList.stream().forEach(item -> {
            UserGoodsBagTurnover turnover = new UserGoodsBagTurnover();
            turnover.setLsh(item.getId());
            turnover.setUserId(storeOrderVo.getUserId());
            turnover.setGoodsId(item.getGoodsId());
            turnover.setTotal(item.getNum());
            turnover.setCreateTime(new Date());

            // 添加流水记录
            turnoverMapper.insert(turnover);

            // 拷贝属性
            UserGoodsBag userGoodsBag = new UserGoodsBag();
            BeanUtils.copyBeanProp(userGoodsBag, turnover);

            // 添加用户背包道具
            userGoodsBagMapper.addOrUpdate(userGoodsBag);
        });
    }

    @Override
    public int updateUserGoodsTotal(HitCallVo hitCallVo) {
        return userGoodsBagMapper.updateUserGoodsTotal(hitCallVo);
    }

    @Override
    public UserGoodsBag searchUserGoods(HitCallVo hitCallVo) {
        return userGoodsBagMapper.searchUserGoods(hitCallVo);
    }
}
