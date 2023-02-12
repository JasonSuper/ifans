package com.ifans.search.service;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.ifans.api.store.to.es.StoreGoodsEsModel;
import com.ifans.search.domain.SearchParam;
import com.ifans.search.domain.SearchResult;

import java.util.List;

public interface StoreGoodsSearchService {

    /**
     * 根据param构建DSL语句，进行查询
     */
    SearchResponse<StoreGoodsEsModel> search(SearchParam param);

    /**
     * 格式化ES的查询结果，以便返回给前端
     */
    SearchResult formatSearchResponse(SearchParam searchParam, SearchResponse<StoreGoodsEsModel> response);

    /**
     * 商品推荐搜索
     *
     * @param keyWord 搜索关键字
     * @return 推荐列表
     */
    List<String> suggest(String keyWord);

    List<String> searchRandomGoodsName();
}
