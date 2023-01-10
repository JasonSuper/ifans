package com.ifans.activiti.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 前端渲染bpmnjs需要的数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessBpmnEntity {

    /**
     * 当前正执行节点id
     */
    private Set<String> activeActivityIds;

    /**
     * 已执行节点id
     */
    private Set<String> executedActivityIds;

    /**
     * 高亮线id（流程已走过的线）
     */
    private Set<String> highlightedFlowIds;

    /**
     * 流程xml文件 字符串
     */
    private String modelXml;

    /**
     * 流程名称
     */
    private String modelName;
}

