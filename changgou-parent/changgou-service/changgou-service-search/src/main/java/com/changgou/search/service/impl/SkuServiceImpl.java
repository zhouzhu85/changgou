package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import entity.Result;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuEsMapper skuEsMapper;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public void importSku() {
        //调用changgou-service-goods微服务
        Result<List<Sku>> skuListResult = skuFeign.findByStatus("1");
        //将数据转换成search.Sku
        List<SkuInfo> skuInfos = JSON.parseArray(JSON.toJSONString(skuListResult.getData()), SkuInfo.class);

        for (SkuInfo skuInfo : skuInfos) {
            Map<String, Object> specMap = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(specMap);
        }
        skuEsMapper.saveAll(skuInfos);
    }

    /**
     * 搜索
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {

        //搜索条件封装
        NativeSearchQueryBuilder nativeSearchQueryBuilder = buildBasicQuery(searchMap);

        //集合搜索
        Map<String, Object> resultMap = searchList(nativeSearchQueryBuilder);

//        //分类分组查询
//        //当用户选择了分类，将分类作为搜索条件，则不需要对分类进行分组搜索，因为分组搜索的数据是用于显示分类搜索条件
//        if (searchMap==null || StringUtils.isEmpty(searchMap.get("category"))) {
//            List<String> categoryList = searchCategoryList(nativeSearchQueryBuilder);
//            resultMap.put("categoryList", categoryList);
//        }
//        if (searchMap==null || StringUtils.isEmpty(searchMap.get("brand"))) {
//            List<String> brandList = searchBrandList(nativeSearchQueryBuilder);
//            resultMap.put("brandList", brandList);
//        }
//
//        final Map<String, Set<String>> specList = searchSpecList(nativeSearchQueryBuilder);
//        resultMap.put("specList",specList);

        //分组搜索实现
         Map<String, Object> groupMap = searchGroupList(nativeSearchQueryBuilder, searchMap);
         resultMap.putAll(groupMap);
        return resultMap;
    }

    /**
     * 分组查询（分类分组、品牌分组、规格分组）
     * @param nativeSearchQueryBuilder
     * @return
     */
    public Map<String,Object> searchGroupList(NativeSearchQueryBuilder nativeSearchQueryBuilder,Map<String,String> searchMap){
        //分组查询集合
        if (searchMap==null || StringUtils.isEmpty(searchMap.get("category"))){
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName"));
        }
        if (searchMap==null || StringUtils.isEmpty(searchMap.get("brand"))) {
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName"));
        }
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword"));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);
        //定义一个map，存储所有分组结果
        HashMap<String, Object> groupMapResult = new HashMap<>();

        //获取分组数据
        if (searchMap==null || StringUtils.isEmpty(searchMap.get("category"))) {
            StringTerms categoryTerms = aggregatedPage.getAggregations().get("skuCategory");
            //获取分类分组集合数据
            List<String> categoryList=getGroupList(categoryTerms);
            groupMapResult.put("categoryList",categoryList);
        }
        if (searchMap==null || StringUtils.isEmpty(searchMap.get("brand"))) {
            StringTerms brandTerms = aggregatedPage.getAggregations().get("skuBrand");
            //获取品牌分组集合数据
            List<String> brandList = getGroupList(brandTerms);
            groupMapResult.put("brandList",brandList);
        }
        StringTerms specTerms = aggregatedPage.getAggregations().get("skuSpec");
        //获取规格分组集合数据
        List<String> specList = getGroupList(specTerms);
        //合并规格集合数据
        Map<String, Set<String>> specMap = putAllSpec(specList);
        groupMapResult.put("specList",specMap);
        return groupMapResult;
    }

    /**
     * 获取分组集合数据
     * @param stringTerms
     * @return
     */
    public List<String> getGroupList(StringTerms stringTerms){
        List<String> groupList=new ArrayList<>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            String feildName = bucket.getKeyAsString();
            groupList.add(feildName);
        }
        return groupList;
    }

    /**
     * 合并规格分组数据
     * @param specList
     * @return
     */
    private Map<String,Set<String>> putAllSpec(List<String> specList){
        //合并后的规格数据
        Map<String,Set<String>> allspec=new HashMap<>();

        for (String spec : specList) {
            //将json字符串转换成map
            Map<String,String> specMap = JSON.parseObject(spec, Map.class);
            //合并流程
            for (Map.Entry<String, String> entry : specMap.entrySet()) {
                //规格名字
                String key = entry.getKey();
                //规格值
                String value = entry.getValue();

                Set<String> specSet = allspec.get(key);
                if (specSet==null){
                    specSet=new HashSet<>();
                }
                specSet.add(value);
                allspec.put(key,specSet);
            }
        }
        return allspec;
    }
    /**
     * 搜索条件封装
     * @param searchMap
     * @return
     */
    private NativeSearchQueryBuilder buildBasicQuery(Map<String, String> searchMap) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder=new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();
        if (searchMap!=null && searchMap.size()>0){
            String keywords = searchMap.get("keywords");
            //如果关键词不为空，则搜索关键词数据
            if (!StringUtils.isEmpty(keywords)){
               // nativeSearchQueryBuilder.withQuery(QueryBuilders.queryStringQuery(keywords).field("name"));
                boolQueryBuilder.must(QueryBuilders.queryStringQuery(keywords).field("name"));
            }
            //分类过滤搜索
            if (!StringUtils.isEmpty(searchMap.get("category"))){
                boolQueryBuilder.must(QueryBuilders.termQuery("categoryName",searchMap.get("category")));
            }
            //品牌过滤搜索
            if (!StringUtils.isEmpty(searchMap.get("brand"))){
                boolQueryBuilder.must(QueryBuilders.termQuery("brandName",searchMap.get("brand")));
            }
            //规格过滤搜索
            for (Map.Entry<String, String> entry : searchMap.entrySet()) {
                String key = entry.getKey();
                //如果key以spec_开始，则表示规格筛选查询
                if (key.startsWith("spec_")){
                    //规格条件的值
                    String value = entry.getValue();
                    //spec_前5个要去掉
                    boolQueryBuilder.must(QueryBuilders.termQuery("specMap."+key.substring(5)+".keyword",value));
                }
            }
            String price = searchMap.get("price");
            if (!StringUtils.isEmpty(price)){
                price=price.replace("元","").replace("以上","");
                String[] prices = price.split("-");
                if (prices!=null && prices.length>0){
                    //大于第一个价格
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gt(Integer.parseInt(prices[0])));
                    if (prices.length==2){
                        //小于第二个价格
                        boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lte(Integer.parseInt(prices[1])));
                    }
                }
            }
        }
        //分页，用户如果不传分页参数，则默认第一页
        Integer pageNum=coverterPage(searchMap);
        //默认查询的数据条数
        Integer size=30;
        nativeSearchQueryBuilder.withPageable(PageRequest.of(pageNum-1,size));

        //排序
        String sortField = searchMap.get("sortField");
        String sortRule = searchMap.get("sortRule");
        //指定排序的域和规则（sortField: 域，sortRule：规则（asc,desc））
        if (!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule)){
            nativeSearchQueryBuilder.withSort(new FieldSortBuilder(sortField).order(SortOrder.valueOf(sortRule)));
        }

        //boolQueryBuildert填充给nativeSearchQueryBuilder
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
        return nativeSearchQueryBuilder;
    }

    /**
     * 接收前端传入的分页参数
     * @param searchMap
     * @return
     */
    private Integer coverterPage(Map<String,String> searchMap){
        if (searchMap!=null){
            String pageNum = searchMap.get("pageNum");
            try {
                return Integer.parseInt(pageNum==null?"1":pageNum);
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        return 1;
    }
    /**
     * 结果集搜索
     * @param nativeSearchQueryBuilder
     * @return
     */
    private Map<String, Object> searchList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        /**高亮配置**/
        //指定高亮域
        HighlightBuilder.Field field=new HighlightBuilder.Field("name");
        //设置前缀
        field.preTags("<em style=\"color:red;\">");
        //设置后缀
        field.postTags("</em>");
        //碎片长度 关键词数据的长度
        field.fragmentSize(100);
        //添加高亮
        nativeSearchQueryBuilder.withHighlightFields(field);
        //搜索结果分页响应
        //AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(
                nativeSearchQueryBuilder.build(),
                SkuInfo.class,
                //执行搜索之后，将数据结果集封装到该对象中
                new SearchResultMapper() {
                    @Override
                    public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                        //存储所有转换后的高亮数据对象
                        List<T> list=new ArrayList<>();
                        //循环所有数据
                        for (SearchHit hit:searchResponse.getHits()){
                            //获取非高亮数据
                            SkuInfo skuInfo = JSON.parseObject(hit.getSourceAsString(), SkuInfo.class);
                            //获取高亮数据
                            HighlightField highlightField = hit.getHighlightFields().get("name");
                            if (highlightField!=null && highlightField.getFragments()!=null){
                                //高亮数据读取
                                Text[] fragments = highlightField.getFragments();
                                StringBuffer buffer=new StringBuffer();
                                for (Text fragment:fragments){
                                    buffer.append(fragment.toString());
                                }
                                //非高亮数据中指定的域替换成高亮数据
                                skuInfo.setName(buffer.toString());
                            }
                            //将高亮数据添加到集合中
                            list.add((T)skuInfo);
                        }
                        /**
                         * 参数1 ：搜索的集合数据
                         * 参数2 ：分页对象信息
                         * 参数3 ：搜索记录的总条数
                         */
                        return new AggregatedPageImpl<>(list,pageable,searchResponse.getHits().getTotalHits());
                    }
                });

        //分页参数-总记录数
        long totalElements = page.getTotalElements();
        //总页数
        int totalPages = page.getTotalPages();
        //获取数据集合
        List<SkuInfo> contents = page.getContent();
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("rows",contents);
        resultMap.put("total",totalElements);
        resultMap.put("totalPages",totalPages);
        return resultMap;
    }

    /**
     * 分类分组查询
     * @param nativeSearchQueryBuilder
     * @return
     */
    private List<String> searchCategoryList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        /**
         * 分组查询分类集合
         * addAggregation方法：添加一个聚合操作，参数1是取别名，参数2是表示根据哪个域进行分组查询
         */
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName"));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);

        /**
         * 获取分组数据
         * aggregatedPage.getAggregations()：获取的是集合，可以根据多个域进行分组
         * get("skuCategory")：获取指定域的集合数 [手机，家用电器，手机配件]
         */
        StringTerms stringTerms = aggregatedPage.getAggregations().get("skuCategory");
        List<String> categoryList = new ArrayList<>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            //其中的一个分类名字
            String categoryName = bucket.getKeyAsString();
            categoryList.add(categoryName);
        }
        return categoryList;
    }
    /**
     * 品牌分组查询
     * @param nativeSearchQueryBuilder
     * @return
     */
    private List<String> searchBrandList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        /**
         * 分组查询品牌集合
         * addAggregation方法：添加一个聚合操作，参数1是取别名，参数2是表示根据哪个域进行分组查询
         */
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName"));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);

        /**
         * 获取分组数据
         * aggregatedPage.getAggregations()：获取的是集合，可以根据多个域进行分组
         * get("skuCategory")：获取指定域的集合数 [手机，家用电器，手机配件]
         */
        StringTerms stringTerms = aggregatedPage.getAggregations().get("skuBrand");
        List<String> brandList = new ArrayList<>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            //其中的一个分类名字
            String brandName = bucket.getKeyAsString();
            brandList.add(brandName);
        }
        return brandList;
    }
    /**
     * 品牌分组查询
     * @param nativeSearchQueryBuilder
     * @return
     */
    private Map<String, Set<String>> searchSpecList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        /**
         * 分组查询品牌集合
         * addAggregation方法：添加一个聚合操作，参数1是取别名，参数2是表示根据哪个域进行分组查询
         */
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword").size(10000));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);

        /**
         * 获取分组数据
         * aggregatedPage.getAggregations()：获取的是集合，可以根据多个域进行分组
         * get("skuCategory")：获取指定域的集合数 [手机，家用电器，手机配件]
         */
        StringTerms stringTerms = aggregatedPage.getAggregations().get("skuSpec");
        List<String> specList = new ArrayList<>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            //其中的一个分类名字
            String specName = bucket.getKeyAsString();
            specList.add(specName);
        }
        //合并后的规格数据
        Map<String,Set<String>> allspec=new HashMap<>();

        for (String spec : specList) {
            //将json字符串转换成map
            Map<String,String> specMap = JSON.parseObject(spec, Map.class);
            //合并流程
            for (Map.Entry<String, String> entry : specMap.entrySet()) {
                //规格名字
                String key = entry.getKey();
                //规格值
                String value = entry.getValue();

                Set<String> specSet = allspec.get(key);
                if (specSet==null){
                    specSet=new HashSet<>();
                }
                specSet.add(value);
                allspec.put(key,specSet);
            }
        }
        return allspec;
    }
    //    @Override
//    public Map<String,Object> search(Map<String, String> searchMap) {
//        //1.获取关键字
//        String keywords = searchMap.get("keywords");
//
//        if (StringUtils.isEmpty(keywords)) {
//            //赋值一个默认的值
//            keywords = "华为";
//        }
//        //2.创建查询对象的构建对象
//        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
//
//        //3.设置查询的条件
//        //设置分组条件（以商品分类名称为组）
//        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategorygroup").field("categoryName").size(50));
//        //设置分组条件（以商品品牌为组）
//        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrandgroup").field("brandName").size(50));
//        //设置分组条件（以商品规格为组）
//        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpecgroup").field("spec.keyword").size(100));
//        // 设置关键字
//        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("name", keywords));
//        //条件过滤
//        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
//        //品牌过滤
//        if (!StringUtils.isEmpty(searchMap.get("brand"))){
//            boolQueryBuilder.filter(QueryBuilders.termQuery("brandName",searchMap.get("brand")));
//        }
//        //分类过滤
//        if (!StringUtils.isEmpty(searchMap.get("category"))){
//            boolQueryBuilder.filter(QueryBuilders.termQuery("categoryName",searchMap.get("category")));
//        }
//        //构建过滤查询
//        nativeSearchQueryBuilder.withFilter(boolQueryBuilder);
//
//        //4.构建查询对象
//        NativeSearchQuery query = nativeSearchQueryBuilder.build();
//
//
//        //5.执行查询
//        AggregatedPage<SkuInfo> skuPage = elasticsearchTemplate.queryForPage(query, SkuInfo.class);
//        //获取商品分类分组的结果
//        StringTerms stringTermsCategory = (StringTerms) skuPage.getAggregation("skuCategorygroup");
//        //获取商品品牌分组的结果
//        StringTerms stringTermsBrand = (StringTerms) skuPage.getAggregation("skuBrandgroup");
//        //获取商品规格分组的结果
//        StringTerms stringTermsSpec = (StringTerms) skuPage.getAggregation("skuSpecgroup");
//
//        List<String> catgegoryList = getStringsList(stringTermsCategory);
//
//        List<String> brandList = getStringsList(stringTermsBrand);
//
//        Map<String,Set<String>> specList=getStringsSetMap(stringTermsSpec);
//
//        //6.返回结果
//        Map resultMap = new HashMap();
//        resultMap.put("categoryList", catgegoryList);
//        resultMap.put("brandList", brandList);
//        resultMap.put("specList",specList);
//        //获取数据结果集
//        resultMap.put("rows", skuPage.getContent());
//        //分页参数-总记录数
//        resultMap.put("total", skuPage.getTotalElements());
//        //总页数
//        resultMap.put("totalPages", skuPage.getTotalPages());
//        return resultMap;
//    }

    /**
     * 获取分类、品牌列表数据
     * @param stringTerms
     * @return
     */
    private List<String> getStringsList(StringTerms stringTerms) {
        List<String> list = new ArrayList<>();

        if (stringTerms != null) {
            for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
                String keyAsString = bucket.getKeyAsString();
                list.add(keyAsString);
            }
        }
        return list;
    }

}

