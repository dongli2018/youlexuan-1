package com.offcn.task;

import com.offcn.mapper.TbSeckillGoodsMapper;
import com.offcn.pojo.TbSeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * All rights Reserved, Designed By www.info4z.club
 * <p>title:com.offcn.task</p>
 * <p>ClassName:SeckillTask</p>
 * <p>Description:TODO(请用一句话描述这个类的作用)</p>
 * <p>Compony:Info4z</p>
 * author:poker_heart
 * date:2019/12/11
 * version:1.0
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Component
public class SeckillTask {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;
    //监视缓存中过期的秒杀商品
    @Scheduled(cron = "* * * * * *")
    public void removeSeckillGoods(){
        List<TbSeckillGoods> seckillGoods = redisTemplate.boundHashOps("seckillGoods").values();
        for (TbSeckillGoods seckillGood : seckillGoods) {
            if(seckillGood.getEndTime().getTime() < new Date().getTime()){
                //同步数据库
                seckillGoodsMapper.updateByPrimaryKey(seckillGood);
                //移除
                redisTemplate.boundHashOps("seckillGoods").delete(seckillGood.getId());
                System.out.println("移除"+seckillGood.getId());
            }
        }
    }
}
