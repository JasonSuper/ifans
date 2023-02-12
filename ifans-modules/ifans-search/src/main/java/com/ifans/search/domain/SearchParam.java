package com.ifans.search.domain;

import lombok.Data;

import java.util.List;

@Data
public class SearchParam {

    /**
     * 关键词
     */
    private String matchWord;

    /**
     * 价格区间
     * _500/1_500/500_
     */
    private String price;

    /**
     * 排序字段
     * price_desc or price_asc
     */
    private List<String> sorts;

    /**
     * 按照属性进行筛选
     */
    private List<String> attrs;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}
