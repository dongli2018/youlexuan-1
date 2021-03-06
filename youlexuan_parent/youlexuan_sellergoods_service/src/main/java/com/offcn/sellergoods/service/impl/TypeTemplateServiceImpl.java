package com.offcn.sellergoods.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.mapper.TbSpecificationOptionMapper;
import com.offcn.mapper.TbTypeTemplateMapper;
import com.offcn.pojo.TbSpecificationOption;
import com.offcn.pojo.TbSpecificationOptionExample;
import com.offcn.pojo.TbTypeTemplate;
import com.offcn.pojo.TbTypeTemplateExample;
import com.offcn.pojo.TbTypeTemplateExample.Criteria;
import com.offcn.sellergoods.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * type_template服务实现层
 * @author senqi
 *
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;
	@Autowired
	private TbSpecificationOptionMapper tbSpecificationOptionMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 分页
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}		
	}
	
	/**
	 * 分页+查询
	 */
	@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();
		
		if(typeTemplate != null){			
						if(typeTemplate.getName() != null && typeTemplate.getName().length() > 0){
				criteria.andNameLike("%" + typeTemplate.getName() + "%");
			}			if(typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0){
				criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
			}			if(typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0){
				criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
			}			if(typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0){
				criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
			}
		}
		
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)typeTemplateMapper.selectByExample(example);
		//添加缓存,为商品检索准备
		List<TbTypeTemplate> tbTypeTemplates =null;
		//品牌
		Set brandKeys = redisTemplate.boundHashOps("brandList").keys();
		if(brandKeys.size()>0){

		}else {
			//加缓存
			tbTypeTemplates = typeTemplateMapper.selectByExample(null);
			for (TbTypeTemplate tbTypeTemplate : tbTypeTemplates) {
				List<Map> map = JSON.parseArray(tbTypeTemplate.getBrandIds(), Map.class);
				redisTemplate.boundHashOps("brandList").put(tbTypeTemplate.getId(),map);
			}
		}
		//规格
		Set specKeys = redisTemplate.boundHashOps("specList").keys();
		if(brandKeys.size()>0){

		}else {
			//加缓存

			if(tbTypeTemplates == null){
				tbTypeTemplates = typeTemplateMapper.selectByExample(null);
			}

			for (TbTypeTemplate tbTypeTemplate : tbTypeTemplates) {
				List<Map> maps = selectOption(tbTypeTemplate.getId());
				redisTemplate.boundHashOps("specList").put(tbTypeTemplate.getId(),maps);
			}
		}

		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override
    public List<Map> selectTemplate() {
        return typeTemplateMapper.selectTemplate();
    }

	@Override
	public List<Map> selectOption(Long id) {
		TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
		List<Map> maps = JSON.parseArray(tbTypeTemplate.getSpecIds(), Map.class);
		for (Map map : maps) {
			Long sid = Long.parseLong(map.get("id")+"");
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(sid);
			List<TbSpecificationOption> tbSpecificationOptions = tbSpecificationOptionMapper.selectByExample(example);
			map.put("options",tbSpecificationOptions);
		}
		return maps;
	}

}
