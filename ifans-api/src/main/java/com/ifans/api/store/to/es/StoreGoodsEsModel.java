package com.ifans.api.store.to.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 道具信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreGoodsEsModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 道具ID
     */
    private String id;

    /**
     * 道具名称
     */
    private String goodsName;

    /**
     * 图标
     */
    private String icon;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 描述
     */
    private String goodsDescribe;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private String delFlag;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * spu商品参数 sku销售参数
     */
    private List<Attrs> attrs;

    @Data
    public static class Attrs {

        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
