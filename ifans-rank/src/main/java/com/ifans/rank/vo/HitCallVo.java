package com.ifans.rank.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 打call请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HitCallVo {

    /**
     * idol的id
     */
    private String idolId;

    /**
     * 道具id
     */
    private String goodsId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 道具数量
     */
    private int nums;

    /**
     * 新的道具总数
     */
    private int newTotal;

    /**
     * 旧的道具总数
     */
    private int oldTotal;

    /**
     * 版本号
     */
    private int version;
}
