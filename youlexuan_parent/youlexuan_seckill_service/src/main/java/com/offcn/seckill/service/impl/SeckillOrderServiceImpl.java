package com.offcn.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.mapper.TbSeckillGoodsMapper;
import com.offcn.mapper.TbSeckillOrderMapper;
import com.offcn.pojo.TbSeckillGoods;
import com.offcn.pojo.TbSeckillOrder;
import com.offcn.seckill.service.SeckillOrderService;
import com.offcn.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;


/**
 * seckill_order服务实现层
 * @author senqi
 *
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;
	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private IdWorker idWorker;
	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	/**
	 * 增加
	 */
	@Override
	public void add(Long id,String name) {
		//通过缓存来处理商品的增减
		TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(id);
		//判定商品是否存在,
		if(seckillGoods == null){
			throw new RuntimeException("商品已卖完,下次再抢吧!");
		}
		if(seckillGoods.getStockCount() == 0 ){
			throw  new RuntimeException("商品已卖完,下次再抢吧!");
		}
		if(seckillGoods.getEndTime().getTime() <= new Date().getTime()){
			throw  new RuntimeException("商品抢购时间已过,下次再抢吧!");
		}
		seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
		redisTemplate.boundHashOps("seckillGoods").put(id,seckillGoods);
		if(seckillGoods.getStockCount() == 0){
			//同步数据库
			seckillGoodsMapper.updateByPrimaryKey(seckillGoods);//同步到数据库
			redisTemplate.boundHashOps("seckillGoods").delete(id);
		}

		//同时在Redis缓存中生成预订单

		TbSeckillOrder seckillOrder = new TbSeckillOrder();
		seckillOrder.setId(idWorker.nextId());
		seckillOrder.setCreateTime(new Date());
		seckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价格
		seckillOrder.setSeckillId(id);
		seckillOrder.setSellerId(seckillGoods.getSellerId());
		seckillOrder.setUserId(name);//设置用户ID
		seckillOrder.setStatus("0");//状态
		redisTemplate.boundHashOps("seckillOrder").put(name,seckillOrder);
	}

    @Override
    public TbSeckillOrder searchOrderFromRedisByUserId(String name) {
		return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(name);
    }

	@Override
	public void updateOrderStatus(String name) {
		//去缓存取订单
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(name);
		seckillOrder.setStatus("1");
		seckillOrder.setPayTime(new Date());
		seckillOrderMapper.insert(seckillOrder);
		redisTemplate.boundHashOps("seckillOrder").delete(name);
	}

	@Override
	public void restore(String name) {
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(name);
		TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillOrder.getSeckillId());
		TbSeckillGoods tbSeckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillOrder.getSeckillId());
		if(seckillGoods == null ){
			//判断时间是否超过
			if(seckillGoods.getEndTime().getTime() > new Date().getTime()){
				seckillGoods = tbSeckillGoods;
			}else {
				//已经过期,回到数据库
				tbSeckillGoods.setStockCount(tbSeckillGoods.getStockCount()+1);
				seckillGoodsMapper.updateByPrimaryKey(tbSeckillGoods);
			}
		}
		if(seckillGoods != null){
			//还原
			seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
			redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(),seckillGoods);
		}
		//再删缓存订单
		redisTemplate.boundHashOps("seckillOrder").delete(name);
	}

}
