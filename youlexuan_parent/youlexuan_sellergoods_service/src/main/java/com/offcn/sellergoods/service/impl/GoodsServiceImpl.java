package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.group.Goods;
import com.offcn.mapper.*;
import com.offcn.pojo.*;
import com.offcn.pojo.TbGoodsExample.Criteria;
import com.offcn.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * goods服务实现层
 * @author senqi
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<Goods> findAll() {
		return null;
//		return goodsMapper.selectByExample(null);
	}

	/**
	 * 分页
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {

		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		//添加状态
		goods.getGoods().setAuditStatus("0");
		//添加商品表
		goodsMapper.insert(goods.getGoods());
		//返回主键值,并设置
		TbGoodsDesc goodsDesc = goods.getGoodsDesc();
		goodsDesc.setGoodsId(goods.getGoods().getId());
		goodsDescMapper.insert(goodsDesc);
		addItem(goods);
	}

	private void addItem(Goods goods) {
		//判定是否启用规格
		if("1".equals(goods.getGoods().getIsEnableSpec())){
			//添加sku
			List<TbItem> items = goods.getItems();
			for (TbItem item : items) {
				setItem(goods, item);
			}
		}else{
			//默认向item表中增加一条数据
			TbItem item = new TbItem();
			item.setPrice(goods.getGoods().getPrice());//价格
			item.setStatus("1");//状态
			item.setIsDefault("1");//是否默认
			item.setNum(999);//库存数量
			item.setSpec("{}");
			setItem(goods, item);
		}
	}

	private void setItem(Goods goods, TbItem item) {
		//添加标题
		String title = goods.getGoods().getGoodsName();
		Map<String,Object> map = JSON.parseObject(item.getSpec(), Map.class);
		for (String s : map.keySet()) {
			title+=" " +map.get(s);
		}
		item.setTitle(title);
		//添加图片
		String itemImages = goods.getGoodsDesc().getItemImages();
		List<Map> maps = JSON.parseArray(itemImages, Map.class);
		if(maps.size()>0){
			item.setImage((String)maps.get(0).get("url"));
		}
		//添加分类
		item.setCategoryid(goods.getGoods().getCategory3Id());
		//时间
		item.setCreateTime(new Date());
		item.setUpdateTime(new Date());
		//商品ID
		item.setGoodsId(goods.getGoods().getId());
		item.setSellerId(goods.getGoods().getSellerId());//商家编号
		//品牌名称
		TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(brand.getName());
		//分类名称
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		item.setCategory(itemCat.getName());
		//商家名称
		TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(seller.getNickName());
		itemMapper.insert(item);
	}


	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		goods.getGoods().setAuditStatus("0");//更改数据后重置状态
		Long id = goods.getGoods().getId();
		//更改三个表
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		//item表需要先删除在添加
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		itemMapper.deleteByExample(example);
		addItem(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods = new Goods();
		//查三个表goods,goodsDesc,item
		//goods
		TbGoods tbgoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbgoods);
		//goodDesc
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(tbGoodsDesc);
		//item
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> tbItems = itemMapper.selectByExample(example);
		goods.setItems(tbItems);
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(goods);
		}		
	}
	
	/**
	 * 分页+查询
	 */
	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();

		if(goods != null){
						if(goods.getSellerId() != null && goods.getSellerId().length() > 0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}			if(goods.getGoodsName() != null && goods.getGoodsName().length() > 0){
				criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
			}			if(goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0){
				criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
			}			if(goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0){
				criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
			}			if(goods.getCaption() != null && goods.getCaption().length() > 0){
				criteria.andCaptionLike("%" + goods.getCaption() + "%");
			}			if(goods.getSmallPic() != null && goods.getSmallPic().length() > 0){
				criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
			}			if(goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0){
				criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
			}			if(goods.getIsDelete() != null && goods.getIsDelete().length() > 0){
				criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
			}
						criteria.andIsDeleteIsNull();
		}

		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void uploadStatus(Long[] ids, String status) {
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

	@Override
	public List<TbItem> findItemById(Long[] ids) {
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");
		criteria.andGoodsIdIn(Arrays.asList(ids));
		return  itemMapper.selectByExample(example);
	}

}
