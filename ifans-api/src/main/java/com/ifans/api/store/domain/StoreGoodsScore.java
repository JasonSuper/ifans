package com.ifans.api.store.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("store_goods_score")
public class StoreGoodsScore implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 道具ID
     */
    @TableField("goods_id")
    private String goodsId;

    /**
     * 道具排行榜热度值
     */
    @TableField("hot_score")
    private String hotScore;

    @TableField("create_by")
    private String createBy;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_by")
    private String updateBy;

    @TableField("update_time")
    private Date updateTime;

}
