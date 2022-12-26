package com.ifans.activiti.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 请假申请单
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Qj implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 申请人Id
     */
    private String uaerId;

    /**
     * 申请人名字
     */
    private String uaerName;

    /**
     * 请假单名称
     */
    private String qjName;

    /**
     * 请假填数
     */
    private Double num;

    /**
     * 预计开始时间
     */
    private Date beginDate;

    /**
     * 预计结束时间
     */
    private Date endData;

    /**
     * 请假原因
     */
    private String reson;
}
