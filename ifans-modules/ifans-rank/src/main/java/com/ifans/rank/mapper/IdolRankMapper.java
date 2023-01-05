package com.ifans.rank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ifans.rank.domain.IdolRank;
import org.apache.ibatis.annotations.Param;

public interface IdolRankMapper extends BaseMapper<IdolRank> {

    IPage<IdolRank> pageList(IPage<?> page, Integer status);

    int upRankHot(@Param("idolId") String idolId, @Param("hot") int hot);
}
