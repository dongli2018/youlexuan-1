package com.offcn.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.info4z.club
 * <p>title:com.offcn.search.service.impl</p>
 * <p>ClassName:ItemSearchServiceImpl</p>
 * <p>Description:TODO(请用一句话描述这个类的作用)</p>
 * <p>Compony:Info4z</p>
 * author:poker_heart
 * date:2019/11/26
 * version:1.0
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Service
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String,Object> map = new HashMap<>();
        //解决`多`空格搜索问题
        String keywords = (String) searchMap.get("keywords");
        if(keywords.contains(" ")){
            keywords.replaceAll(" ","");
        }
        searchMap.put("keywords",keywords);

        highSearch(searchMap, map);
        cateGroup(searchMap,map);

        if (!StringUtils.isEmpty(searchMap.get("category"))) {
            brandAndSpecSearch((String) searchMap.get("category"), map);
        } else {
            //获取分类名,默认第一个,查品牌和规格
            List<String> categoryList = (List<String>) map.get("categoryList");
            if (categoryList.size() > 0) {
                brandAndSpecSearch(categoryList.get(0), map);
            }
        }
        return map;
    }

    @Override
    public void importDate(List<TbItem> list) {
        for (TbItem item : list) {
            Map<String,String> map = JSON.parseObject(item.getSpec(),Map.class);
            Map<String,String> newMap = new HashMap<>();
            for (String key : map.keySet()) {
                newMap.put(Pinyin.toPinyin(key,"").toLowerCase(),map.get(key));
            }
            item.setSpecMap(newMap);
        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }
    @Override
    public void deleteDate(Long[] ids) {
        Criteria criteria = new Criteria("item_goodsid").in(ids);
        Query query = new SimpleQuery();
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    //根据分类名查询品牌,规格
    private void brandAndSpecSearch(String categoryName,Map<String, Object> map){
        Long typeId = (Long) redisTemplate.boundHashOps("itemcat").get(categoryName);
        List<Map> brandList=null,specList=null;
        if(typeId != null){
            brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
            specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
        }
        map.put("brandList",brandList);
        map.put("specList",specList);
    }

    //根据关键字查类,并分组
    private  void cateGroup(Map searchMap, Map<String, Object> map){
        List list = new ArrayList();
        Query query = new SimpleQuery("*:*");
        //添加分组属性
        GroupOptions options = new GroupOptions();
        options.addGroupByField("item_category");
        //关键字
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.setGroupOptions(options);
        query.addCriteria(criteria);
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        GroupResult<TbItem> item_category = page.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> groupEntries = item_category.getGroupEntries();
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> tbItemGroupEntry : content) {
            list.add(tbItemGroupEntry.getGroupValue());
        }

        map.put("categoryList",list);
    }


    //高亮,分类等查询
    private void highSearch(Map searchMap, Map<String, Object> map) {
        //高亮属性设置
        HighlightQuery query = new SimpleHighlightQuery();
        HighlightOptions options = new HighlightOptions();
        options.addField("item_title").setSimplePrefix("<span style='color:red'>").setSimplePostfix("</span>");
        query.setHighlightOptions(options);
        //关键字查询
        Criteria c = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(c);
        //分类查询
        if(! "".equals(searchMap.get("category"))){
            Criteria cateCrite = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery cf = new SimpleFacetQuery(cateCrite);
            query.addFilterQuery(cf);
        }
        //品牌查询
        if(!StringUtils.isEmpty(searchMap.get("brand"))){
            Criteria brandCrite = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery bf = new SimpleFacetQuery(brandCrite);
            query.addFilterQuery(bf);
        }
        //规格查询
        if(!ObjectUtils.isEmpty(searchMap.get("spec"))){
            Map<String,String> smap = (Map) searchMap.get("spec");
            for (String s : smap.keySet()) {
                Criteria specCrite = new Criteria("item_spec_"+ Pinyin.toPinyin(s,"").toLowerCase()).is(smap.get(s));
                FilterQuery sf = new SimpleFacetQuery(specCrite);
                query.addFilterQuery(sf);
            }
        }
        //价格查询
        if(!"".equals(searchMap.get("price"))){
            String price = (String) searchMap.get("price");

                String[] split = price.split("-");
                //大于最小值
                Criteria criteria = new Criteria("item_price").greaterThanEqual(split[0]);
                FilterQuery pf = new SimpleFacetQuery(criteria);
                query.addFilterQuery(pf);
                if(!split[1].equals("*")){
                    //小于最大值
                    Criteria criteria2 = new Criteria("item_price").lessThanEqual(split[1]);
                    FilterQuery pf2 = new SimpleFacetQuery(criteria2);
                    query.addFilterQuery(pf2);
                }
        }
        //分页
        //起始页
        Integer pageNum = (Integer) searchMap.get("pageNum");
        //每页条数
        Integer pageSize = (Integer) searchMap.get("pageSize");
        query.setOffset((pageNum-1)*pageSize);
        query.setRows(pageSize);
        //排序
        String sortName = (String) searchMap.get("sortName");
        String sortValue = (String) searchMap.get("sortValue");
        if(!StringUtils.isEmpty(sortName)){
            Sort sort = null;
            if(sortValue.equals("desc")){
                 sort = new Sort(Sort.Direction.DESC,"item_"+sortName);
            }else if(sortValue.equals("asc")){
                sort = new Sort(Sort.Direction.ASC,"item_"+sortName);
            }
            query.addSort(sort);
        }
        //结果处理
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();
        for (HighlightEntry<TbItem> entry : highlighted) {
            TbItem item = entry.getEntity();
            if(entry.getHighlights().size()>0&&entry.getHighlights().get(0).getSnipplets().size()>0){
                item.setTitle(entry.getHighlights().get(0).getSnipplets().get(0));
            }
        }
        map.put("total",page.getTotalElements());
        map.put("totalPage",page.getTotalPages());
        map.put("rows",page.getContent());
    }
}
