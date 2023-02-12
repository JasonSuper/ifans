package com.ifans.search.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.ifans.api.store.to.es.StoreGoodsEsModel;
import com.ifans.common.core.util.R;
import com.ifans.search.domain.SearchParam;
import com.ifans.search.domain.SearchResult;
import com.ifans.search.service.StoreGoodsSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品搜索
 */
@RestController
@RequestMapping("/goods")
@RequiredArgsConstructor
public class StoreGoodSearchController {

    private final StoreGoodsSearchService storeGoodsSearchServiceImpl;

    /**
     * 商品推荐搜索
     *
     * @param keyWord 搜索关键字
     * @return 推荐列表
     */
    @GetMapping("/suggestGoods/{keyWord}")
    public R suggestGoods(@PathVariable String keyWord) {
        List<String> suggestWordList = storeGoodsSearchServiceImpl.suggest(keyWord);
        if (suggestWordList == null) {
            return R.failed("查询服务异常");
        }
        return R.ok(suggestWordList);
    }

    /**
     * 随机返回5个商品名称
     */
    @GetMapping("/searchRandomGoodsName")
    public R searchRandomGoodsName() {
        List<String> stringList = storeGoodsSearchServiceImpl.searchRandomGoodsName();
        Map<String, Object> result = new HashMap<>();
        if (stringList != null) {
            result.put("code", "200");
            result.put("data", stringList);
            result.put("msg", "成功");
        } else {
            result.put("code", "500");
            result.put("msg", "失败");
        }
        return R.ok(result);
    }

    /**
     * 搜索相关商品
     *
     * @param param 搜索条件
     */
    @PostMapping("/search")
    public R search(@RequestBody SearchParam param) {
        // 根据param构建DSL语句，进行查询
        SearchResponse<StoreGoodsEsModel> response = storeGoodsSearchServiceImpl.search(param);
        // 格式化ES的查询结果，以便返回给前端
        SearchResult result = storeGoodsSearchServiceImpl.formatSearchResponse(param, response);
        return R.ok(result);
    }
}
