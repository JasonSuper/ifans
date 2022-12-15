package com.ifans.rank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ifans.api.rank.domain.UserGoodsBag;
import com.ifans.api.rank.vo.UserGoodsBagVo;
import com.ifans.rank.vo.HitCallVo;

import java.util.List;

public interface UserGoodsBagMapper extends BaseMapper<UserGoodsBag> {

    List<UserGoodsBagVo> selectByUserId(String userid);

    int addOrUpdate(UserGoodsBag userGoodsBag);

    int updateUserGoodsTotal(HitCallVo hitCallVo);
}
