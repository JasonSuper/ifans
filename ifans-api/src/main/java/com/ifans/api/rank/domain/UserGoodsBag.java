package com.ifans.api.rank.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_goods_bag")
public class UserGoodsBag implements Serializable {
    private static final long serialVersionUID = 1L;

    /*@TableId(type = IdType.ASSIGN_ID)
    private String id;*/

    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;

    /**
     * 道具id
     */
    @TableField("goods_id")
    private String goodsId;

    /**
     * 道具数量
     */
    @TableField("total")
    private int total;

    /**
     * 版本号
     */
    @TableField("version")
    private int version;
}
