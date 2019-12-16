package com.offcn.cart.service;

import com.offcn.group.Cart;

import java.util.List;

/**
 * All rights Reserved, Designed By www.info4z.club
 * <p>title:com.offcn.cart.service</p>
 * <p>ClassName:CartService</p>
 * <p>Description:TODO(请用一句话描述这个类的作用)</p>
 * <p>Compony:Info4z</p>
 * author:poker_heart
 * date:2019/12/5
 * version:1.0
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
public interface CartService {
    List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);
    List<Cart> findCartListFromRedis(String name);
    void saveCartListToRedis(String name,List<Cart> cartList);
    List<Cart> mergeList(List<Cart> list1,List<Cart> list2);
}
