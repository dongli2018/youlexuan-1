package com.offcn.page.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.mapper.TbGoodsDescMapper;
import com.offcn.mapper.TbGoodsMapper;
import com.offcn.mapper.TbItemCatMapper;
import com.offcn.mapper.TbItemMapper;
import com.offcn.page.service.ItemPageService;
import com.offcn.pojo.TbGoods;
import com.offcn.pojo.TbGoodsDesc;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.info4z.club
 * <p>title:com.offcn.page.service.impl</p>
 * <p>ClassName:ItemPageServiceImpl</p>
 * <p>Description:TODO(请用一句话描述这个类的作用)</p>
 * <p>Compony:Info4z</p>
 * author:poker_heart
 * date:2019/11/30
 * version:1.0
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Service
public class ItemPageServiceImpl implements  ItemPageService {
    @Value("${pagedir}")
    private String pagedir;
    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Override
    public boolean genItemHtml(Long goodsId) {
        Writer out = null;
       try {
           // 获取configuration
           Configuration configuration = freeMarkerConfig.getConfiguration();
          //获取模板
           Template template = configuration.getTemplate("item.ftl");
           Map<String,Object> map = new HashMap<>();
           //获取数据
           //1 查询商品goods
           TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
           //2查询商品详情表
           TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
           //3.查询分类名
           String name1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
           String name2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
           String name3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
           //4.查询sku
           TbItemExample example = new TbItemExample();
           TbItemExample.Criteria criteria = example.createCriteria();
           criteria.andStatusEqualTo("1");
           criteria.andGoodsIdEqualTo(goodsId);
           example.setOrderByClause("is_default desc");
           List<TbItem> itemList = itemMapper.selectByExample(example);

           map.put("goods",goods);
           map.put("goodsDesc",goodsDesc);
           map.put("name1",name1);
           map.put("name2",name2);
           map.put("name3",name3);
           map.put("itemList",itemList);

           //设置储存位置
           out = new FileWriter(new File(pagedir+goodsId+".html"));
           //输入
           template.process(map,out);
           return true;
       }catch (Exception e){
           e.printStackTrace();
           return false;
       }finally {
           if(out != null){
               try {
                   out.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
    }

    @Override
    public boolean deleteItemHtml(Long[] ids) {
        try {
            for (Long id : ids) {
                File file = new File(pagedir+id+".html");
                file.delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
