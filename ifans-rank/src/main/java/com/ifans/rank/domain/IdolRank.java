package com.ifans.rank.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 排行榜
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("idol_rank")
public class IdolRank {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 头像
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 姓名
     */
    @TableField("idol_name")
    private String idolName;

    /**
     * 热度
     */
    @TableField("rank_hot")
    private long rankHot;

}
