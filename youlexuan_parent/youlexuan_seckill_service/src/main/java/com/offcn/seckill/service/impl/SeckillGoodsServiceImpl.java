package com.offcn.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.mapper.TbSeckillGoodsMapper;
import com.offcn.pojo.TbSeckillGoods;
import com.offcn.pojo.TbSeckillGoodsExample;
import com.offcn.pojo.TbSeckillGoodsExample.Criteria;
import com.offcn.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;

/**
 * seckill_goods服务实现层
 * @author senqi
 *
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	@Autowired
	private RedisTemplate redisTemplate;
    @Override
    public List<TbSeckillGoods> findList() {
        //从Redis里面找
		List<TbSeckillGoods> seckillGoods = redisTemplate.boundHashOps("seckillGoods").values();
		if(seckillGoods == null || seckillGoods.size() == 0){
			//从数据库查
			TbSeckillGoodsExample example = new TbSeckillGoodsExample();
			Criteria c = example.createCriteria();
			//状态正常
			c.andStatusEqualTo("1");
			//在秒杀的时间内
			c.andStartTimeLessThanOrEqualTo(new Date());
			c.andEndTimeGreaterThanOrEqualTo(new Date());
			//必须有库存
			c.andStockCountGreaterThan(0);
			seckillGoods = seckillGoodsMapper.selectByExample(example);
			//存到Redis,方便下次查
			for (TbSeckillGoods seckillGood : seckillGoods) {
				redisTemplate.boundHashOps("seckillGoods").put(seckillGood.getId(),seckillGood);
                System.out.println("导入Redis");
			}
		}
		return seckillGoods;
	}

	@Override
	public TbSeckillGoods findOne(Long id) {
		//从缓存取
		return (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(id);
	}
}
