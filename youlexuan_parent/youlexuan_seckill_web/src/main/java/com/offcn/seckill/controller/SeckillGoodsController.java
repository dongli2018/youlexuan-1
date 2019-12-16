package com.offcn.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.pojo.TbSeckillGoods;
import com.offcn.seckill.service.SeckillGoodsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * seckill_goodscontroller
 * @author senqi
 *
 */
@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {

	@Reference
	private SeckillGoodsService seckillGoodsService;

	/**
	 * 查找
	 * @param
	 * @return
	 */
	@RequestMapping("/findList")
	public List<TbSeckillGoods> findList(){
		return  seckillGoodsService.findList();
	}
	@RequestMapping("findOne")
	private TbSeckillGoods findOne(Long id){
		return seckillGoodsService.findOne(id);
	}
}
