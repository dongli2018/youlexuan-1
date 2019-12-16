package com.offcn.seckill.service;

import com.offcn.entity.PageResult;
import com.offcn.pojo.TbSeckillOrder;

import java.util.List;

/**
 * seckill_order服务层接口
 * @author senqi
 *
 */
public interface SeckillOrderService {

	/**
	 * 增加
	*/
	public void add(Long id,String name);


    TbSeckillOrder searchOrderFromRedisByUserId(String name);

	void updateOrderStatus( String name);

	void restore(String name);
}
