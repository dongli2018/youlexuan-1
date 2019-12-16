package com.offcn.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.cart.service.CartService;
import com.offcn.group.Cart;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * All rights Reserved, Designed By www.info4z.club
 * <p>title:com.offcn.cart.service.impl</p>
 * <p>ClassName:CartServiceImpl</p>
 * <p>Description:TODO(请用一句话描述这个类的作用)</p>
 * <p>Compony:Info4z</p>
 * author:poker_heart
 * date:2019/12/5
 * version:1.0
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //先判定商品的存不存在
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if(item == null){
            throw  new RuntimeException("该商品不存在");
        }
        if(!item.getStatus().equals("1")){
            throw  new RuntimeException("该商品已下架");
        }
        //判断购物车里面有没有这个商家店铺
        Cart cart = searchCartBySellerId(cartList,item.getSellerId());
        //如果没有,就创建商家店铺的购物车
        if(cart==null){
            cart = new Cart();
            cart.setSellerId(item.getSellerId());
            cart.setSellerName(item.getSeller());

            TbOrderItem orderItem = getTbOrderItem(itemId, num, item);

            List<TbOrderItem> orderItemList = new ArrayList<>();
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            cartList.add(cart);
        }//如果有这个商家
        else {
            //判断有没有添加过这个商品
            TbOrderItem orderItem  = searchOrderItemByItemId(cart.getOrderItemList(),itemId);
            //如果没有这个商品
            if(orderItem == null){
                orderItem = getTbOrderItem(itemId, num, item);
                cart.getOrderItemList().add(orderItem);
            }//如果有这个商品
            else {
                //添加数量
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
                //如果用户减少商品
                if(orderItem.getNum() <= 0){
                    cart.getOrderItemList().remove(orderItem);
                }
                //如果用户清空这个商家商品
                if(cart.getOrderItemList().size() <= 0){
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }

    @Override
    public List<Cart> findCartListFromRedis(String name) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(name);
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String name, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(name,cartList);
    }

    @Override
    public List<Cart> mergeList(List<Cart> list1, List<Cart> list2) {
        //把list2合到list1里面
        for (Cart cart : list2) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                list1 = addGoodsToCartList(list1,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return list1;
    }

    private TbOrderItem getTbOrderItem(Long itemId, Integer num, TbItem item) {
        TbOrderItem orderItem = new TbOrderItem();

        orderItem.setItemId(itemId);
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setNum(num);
        orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*num));
        orderItem.setPicPath(item.getImage());
        orderItem.setSellerId(item.getSellerId());
        return orderItem;
    }

    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if(orderItem.getItemId().longValue() == itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }

    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }
}
