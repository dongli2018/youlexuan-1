package com.offcn.seckill.service;

import com.offcn.entity.PageResult;
import com.offcn.pojo.TbSeckillGoods;

import java.util.List;

/**
 * seckill_goods服务层接口
 * @author senqi
 *
 */
public interface SeckillGoodsService {
    List<TbSeckillGoods> findList();

    TbSeckillGoods findOne(Long id);
}
