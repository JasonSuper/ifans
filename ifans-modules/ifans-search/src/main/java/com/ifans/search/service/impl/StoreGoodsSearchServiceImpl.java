package com.ifans.search.service.impl;

import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.*;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.*;
import co.elastic.clients.json.JsonData;
import com.ifans.api.store.to.es.StoreGoodsEsModel;
import com.ifans.common.core.util.StringUtils;
import com.ifans.search.domain.SearchParam;
import com.ifans.search.domain.SearchResult;
import com.ifans.search.service.StoreGoodsSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreGoodsSearchServiceImpl implements StoreGoodsSearchService {

    private final ElasticsearchClient esClient;

    /**
     * 根据param构建DSL语句，进行查询
     * 直接看代码可能比较难理解，利用dsl.json文件来辅助，比较容易理解
     *
     * @param param 搜索条件
     */
    @Override
    public SearchResponse<StoreGoodsEsModel> search(SearchParam param) {
        try {
            // 查询语句构建
            SearchRequest.Builder builder = new SearchRequest.Builder();
            builder.index("store_goods_index");

            // 构建bool query
            BoolQuery.Builder boolQuery = new BoolQuery.Builder();

            if (!StrUtil.isEmpty(param.getMatchWord())) {
                boolQuery.must(mu -> mu.match(m -> m.field("goodsName").query(param.getMatchWord())));
            } else {
                boolQuery.must(mu -> mu.matchAll(new MatchAllQuery.Builder().build()));
            }

            // 构建属性查询
            if (param.getAttrs() != null && param.getAttrs().size() > 0) {
                List<Query> nestedQueryList = new ArrayList<>();
                // 每一个attrs都需要生成一个nested查询
                param.getAttrs().stream().forEach(attrStr -> {
                    Query nestedQuery = NestedQuery.of(n -> n
                            .path("attrs")
                            .query(q -> q.bool(b -> {
                                String[] attrSplit = attrStr.split("_");
                                String[] attrValues = attrSplit[1].split(":");
                                List<FieldValue> diqu = new ArrayList<>();
                                for (String attrValue : attrValues) {
                                    diqu.add(new FieldValue.Builder().stringValue(attrValue).build());
                                }
                                b.must(m -> m.bool(bq -> {
                                    bq.must(mu -> mu.term(t -> t.field("attrs.attrId").value(attrSplit[0])));
                                    bq.must(mu -> mu.terms(ts -> ts.field("attrs.attrValue").terms(ts2 -> ts2.value(diqu))));
                                    return bq;
                                }));
                                return b;
                            }))
                            .scoreMode(ChildScoreMode.None)
                    )._toQuery();
                    nestedQueryList.add(nestedQuery);
                });
                boolQuery.filter(nestedQueryList);
            }

            // 构建价格区间查询
            if (!StrUtil.isEmpty(param.getPrice())) {
                RangeQuery.Builder query = new RangeQuery.Builder().field("price");
                String[] priceRange = param.getPrice().split("_");
                if (priceRange.length == 1) {
                    if (param.getPrice().startsWith("_")) {
                        query.lte(JsonData.of(priceRange[0]));
                    } else {
                        query.gte(JsonData.of(priceRange[0]));
                    }
                } else if (priceRange.length == 2) {
                    // _500会截取成["", "500"]
                    if (!priceRange[0].isEmpty()) {
                        query.gte(JsonData.of(priceRange[0]));
                    }
                    query.lte(JsonData.of(priceRange[1]));
                }
                boolQuery.filter(query.build()._toQuery());
            }

            // bool query构建完成
            builder.query(boolQuery.build()._toQuery());

            // 构建分页语句
            builder.from((param.getPageNum() - 1) * param.getPageSize());
            builder.size(param.getPageSize());

            // 构建高亮语句
            Highlight highlight = Highlight.of(h -> h.fields("goodsName", hf -> hf.preTags("<b style='color:red'>").postTags("</b>")));
            builder.highlight(highlight);

            // 构建排序语句
            if (param.getSorts() != null && param.getSorts().size() > 0) {
                param.getSorts().stream().forEach(s -> {
                    String[] sortParam = s.split("_");
                    SortOptions.Builder sb = new SortOptions.Builder();
                    sb.field(f -> f.field(sortParam[0]).order(sortParam[1] == "asc" ? SortOrder.Asc : SortOrder.Desc));
                    builder.sort(sb.build());
                });
            }

            // 构建attrs嵌套聚合
            builder.aggregations("attrs", ab -> ab
                    .nested(n -> n.path("attrs"))
                    // 使用attrId进行属性聚合，
                    .aggregations("attrIdAgg", a -> a.terms(t -> t.field("attrs.attrId").size(10))
                            // 构建attrName和attrValue的嵌套子聚合，用于获取属性名和属性值
                            .aggregations("attrNameAgg", sona -> sona.terms(t -> t.field("attrs.attrName").size(10)))
                            .aggregations("attrValueAgg", sona -> sona.terms(t -> t.field("attrs.attrValue").size(10)))
                    )
            );

            // 构建DSL查询请求
            SearchResponse<StoreGoodsEsModel> searchResponse = esClient.search(builder.build(), StoreGoodsEsModel.class);
            return searchResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 格式化ES的查询结果，以便返回给前端
     *
     * @param response ES的查询结果
     */
    @Override
    public SearchResult formatSearchResponse(SearchParam searchParam, SearchResponse<StoreGoodsEsModel> response) {
        SearchResult sr = new SearchResult();
        HitsMetadata hits = response.hits();

        // 解析查询到的商品信息
        List<StoreGoodsEsModel> sgemList = response.hits().hits().stream().map(hit -> {
            hit.source().setId(hit.id());
            //设置高亮属性
            if (!StringUtils.isEmpty(searchParam.getMatchWord())) {
                String highLight = hit.highlight().get("goodsName").get(0);
                hit.source().setGoodsName(highLight);
            }
            return hit.source();
        }).collect(Collectors.toList());

        sr.setProduct(sgemList);
        sr.setPageNum(searchParam.getPageNum());

        // 装配分页属性
        long total = (int) response.hits().total().value();
        // 总页数
        Integer totalPages = (int) total % searchParam.getPageSize() == 0 ?
                (int) total / searchParam.getPageSize() : (int) total / searchParam.getPageSize() + 1;

        // 前端页码选择项
        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        sr.setTotalPages(totalPages);
        sr.setTotal(total);
        sr.setPageNavs(pageNavs);

        // 解析涉及到的所有商品属性
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        Aggregate aggregate = response.aggregations().get("attrs");
        NestedAggregate nested = aggregate.nested();
        Map<String, Aggregate> aggregations = nested.aggregations();
        LongTermsAggregate attrIdAgg = aggregations.get("attrIdAgg").lterms();
        List<LongTermsBucket> attrIdAggBucket = attrIdAgg.buckets().array();
        for (LongTermsBucket bucket : attrIdAggBucket) {
            // 解析属性id
            Long attrId = bucket.key();

            // 解析属性名
            Map<String, Aggregate> attrIdAggBucketAgg = bucket.aggregations();
            StringTermsAggregate attrNameAgg = attrIdAggBucketAgg.get("attrNameAgg").sterms();
            String attrName = attrNameAgg.buckets().array().get(0).toString();

            // 解析属性值
            StringTermsAggregate attrValueAgg = attrIdAggBucketAgg.get("attrValueAgg").sterms();
            List<String> attrValues = attrValueAgg.buckets().array()
                    .stream().map(b -> b.key().stringValue()).collect(Collectors.toList());

            // 装配商品属性
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo(attrId, attrName, attrValues);
            attrVos.add(attrVo);
        }
        sr.setAttrs(attrVos);
        return sr;
    }

    /**
     * 商品推荐搜索
     *
     * @param keyWord 搜索关键字
     * @return 推荐列表
     */
    @Override
    public List<String> suggest(String keyWord) {
        Suggester suggest = Suggester.of(m -> m.suggesters("goods_suggest", FieldSuggester.of(a -> a
                        .prefix(keyWord)
                        .completion(c -> c
                                .field("goodsName.suggest")
                                .skipDuplicates(true)
                                .fuzzy(SuggestFuzziness.of(f -> f.fuzziness("2"))))
                ))
        );

        try {
            SearchResponse<StoreGoodsEsModel> response = esClient.search(b -> b
                    .index("store_goods_index")
                    .size(8)
                    .suggest(suggest), StoreGoodsEsModel.class);

            List<String> wordList = new ArrayList<>();
            response.suggest().get("goods_suggest").stream().forEach(s -> {
                CompletionSuggest cs = s.completion();
                List<CompletionSuggestOption> options = cs.options();
                options.stream().forEach(o -> wordList.add(o.text()));
            });
            return wordList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 随机返回5个商品名称
     */
    @Override
    public List<String> searchRandomGoodsName() {
        try {
            ScriptSort scriptSort = new ScriptSort.Builder()
                    .script(sc -> sc.inline(in -> in.source("Math.random()")))
                    .order(SortOrder.Asc)
                    .type(ScriptSortType.Number)
                    .build();

            SearchResponse<StoreGoodsEsModel> response = esClient.search(b -> b
                    .index("store_goods_index")
                    .size(5)
                    .query(q -> q.matchAll(new MatchAllQuery.Builder().build()))
                    .sort(s -> s.script(scriptSort)), StoreGoodsEsModel.class);

            List<String> collect = response.hits().hits().stream().map(hit -> hit.source().getGoodsName()).collect(Collectors.toList());
            return collect;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
