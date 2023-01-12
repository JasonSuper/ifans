package com.ifans.api.activiti.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流程与业务关联
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("act_bus_relation")
public class ActBusRelation {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 唯一编码
     */
    @TableField("unique_code")
    private String uniqueCode;

    /**
     * 关联的业务表名
     */
    @TableField("bus_table_name")
    private String busTableName;

    /**
     * 业务标题表达式
     */
    @TableField("bus_title_exp")
    private String busTitleExp;

    /**
     * 流程状态列名，通过表达式`${}`获取流程变量的值，组成一个标题
     */
    @TableField("act_status_col_name")
    private String actStatusColName;
}
