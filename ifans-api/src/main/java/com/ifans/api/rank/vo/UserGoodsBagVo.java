package com.ifans.api.rank.vo;

import com.ifans.api.rank.domain.UserGoodsBag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGoodsBagVo extends UserGoodsBag {

    private String icon;

    private String goodsName;
}
