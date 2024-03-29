package com.ifans.api.store.domain;

import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 道具信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("store_goods")
public class StoreGoods implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 道具ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 道具名称
     */
    @TableField("goods_name")
    private String goodsName;

    /**
     * 图标
     */
    @TableField("icon")
    private String icon;

    /**
     * 价格
     */
    @TableField("price")
    @JSONField(name = "price", serializeUsing = ToStringSerializer.class)
    private BigDecimal price;

    /**
     * 描述
     */
    @TableField("goods_describe")
    private String goodsDescribe;

    /**
     * 状态（0正常 1停用）
     */
    @TableField("status")
    private String status;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    @TableField("del_flag")
    private String delFlag;

    /**
     * 计量单位
     */
    @TableField("unit")
    private String unit;

    @TableField("create_by")
    private String createBy;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_by")
    private String updateBy;

    @TableField("update_time")
    private Date updateTime;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

}
