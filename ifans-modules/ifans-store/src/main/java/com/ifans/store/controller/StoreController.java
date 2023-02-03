package com.ifans.store.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.FieldSuggester;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.SuggestFuzziness;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import co.elastic.clients.json.JsonData;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifans.api.store.domain.StoreGoods;
import com.ifans.api.store.to.es.StoreGoodsEsModel;
import com.ifans.common.core.util.R;
import com.ifans.store.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/goods")
public class StoreController {

    @Autowired
    private StoreService storeService;
    @Autowired
    private ElasticsearchClient esClient;

    @PostMapping("/list")
    public R list(@RequestBody Page page) {
        IPage<StoreGoods> list = storeService.pageList(page, 0);
        return R.ok(list);
    }

    @GetMapping("/info/{id}")
    public R info(@PathVariable String id) {
        StoreGoods storeGoods = storeService.getById(id);
        return R.ok(JSON.toJSONString(storeGoods), "成功");
    }

    /**
     * 商品推荐搜索
     *
     * @param val 搜索关键字
     * @return 推荐列表
     */
    @GetMapping("/suggestGoods")
    public R suggestGoods(String val) throws IOException {
//        Query query = MatchQuery.of(m -> m
//                .field("name")
//                .query("")
//        )._toQuery();

        Suggester suggest = Suggester.of(m -> m.suggesters("goods_suggest", FieldSuggester.of(a -> a
                        .prefix(val)
                        .completion(c -> c
                                .field("title.suggest")
                                .skipDuplicates(true)
                                .fuzzy(SuggestFuzziness.of(f -> f.fuzziness("2"))))
                ))
        );

        SearchResponse<Void> response = esClient.search(b -> b
                .index("")
                .size(0)
                //.query(query)
                .suggest(suggest), Void.class);
        System.out.println(response);
        return R.ok();
    }

    /**
     * 搜索相关商品
     *
     * @param val 搜索关键字
     * @return 相关商品
     */
    @PostMapping("/search")
    public R search(@RequestParam("val") String val, @RequestBody Page page) throws IOException {

        Query t1 = TermQuery.of(t -> t.field("").value(""))._toQuery();
        Query t2 = TermsQuery.of(t -> t
                .field("")
                .terms(ts -> ts.value(new ArrayList<>()))
        )._toQuery();

        List<Query> nestedQueryList = new ArrayList<>();
        // 每一个attrs都需要生成一个nested查询
        for (int i = 0; i < 10; i++) {
            Query nestedQuery = NestedQuery.of(n -> n
                    .path("")
                    .query(q -> q.bool(b -> {
                                b.must(m -> m.term(t -> t.field("").value("")));
                                b.must(m -> m.terms(ts -> ts.field("").terms(ts2 -> ts2.value(new ArrayList<>()))));
                                return b;
                            })
                    )
                    .scoreMode(ChildScoreMode.None)
            )._toQuery();
            nestedQueryList.add(nestedQuery);
        }

        Query t4 = RangeQuery.of(t -> t.field("price").gte(JsonData.of("")).lte(JsonData.of("")))._toQuery();

        //new BoolQuery.Builder().filter().filter()

        // 构建查询请求
        Query boolQuery = BoolQuery.of(b -> b
                .must(mu -> mu.match(m -> m.field("").query("")))
                .filter(t1, t2, t4)
                .filter(nestedQueryList)
        )._toQuery();

        // 构建高亮属性
        Highlight highlight = Highlight.of(h -> h.fields("", hf -> hf.preTags("<em>").postTags("</em>")));

        // 构建DSL查询请求
        SearchResponse<Object> searchResponse = esClient.search(s -> s
                .index("store_goods_index")
                .query(boolQuery)
                .from(0)
                .size(1)
                .highlight(highlight)
                .sort(sr -> sr.field(f -> f.field("").order(SortOrder.Desc)))
                .aggregations(null), Object.class);


        // -----------------------------------------------------------------------------------------------


        Query query = MatchQuery.of(m -> m
                .field("goodsName")
                .query(val)
        )._toQuery();

        SearchResponse<StoreGoodsEsModel> response = esClient.search(b -> b
                .index("store_goods_index")
                .from((int) page.getPages())
                .size((int) page.getSize())
                .query(query), StoreGoodsEsModel.class);

        page.setRecords(response.hits().hits().stream().map(hit -> {
            hit.source().setId(hit.id());
            return hit.source();
        }).collect(Collectors.toList()));
        page.setTotal(0);

        return R.ok();
    }
}
