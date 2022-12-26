package com.ifans.api.rank.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_goods_bag_turnover")
public class UserGoodsBagTurnover implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;

    /**
     * 流水号
     */
    @TableField("lsh")
    private String lsh;

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
     * 添加时间
     */
    @TableField("create_time")
    private Date createTime;
}
