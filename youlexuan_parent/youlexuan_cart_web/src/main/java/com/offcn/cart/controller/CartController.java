package com.offcn.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.offcn.cart.service.CartService;
import com.offcn.entity.Result;
import com.offcn.group.Cart;
import com.offcn.utils.CookieUtil;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * All rights Reserved, Designed By www.info4z.club
 * <p>title:com.offcn.cart.controller</p>
 * <p>ClassName:CartController</p>
 * <p>Description:TODO(请用一句话描述这个类的作用)</p>
 * <p>Compony:Info4z</p>
 * author:poker_heart
 * date:2019/12/5
 * version:1.0
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @RequestMapping("findCartList")
    public List<Cart> findCartList(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //从cookie里面找
        String cookie_cartList = CookieUtil.getCookieValue(request, "cartList", "utf-8");
        if(cookie_cartList==null || cookie_cartList.equals("")){
            cookie_cartList = "[]";
        }
        //没登录
        if("anonymousUser".equals(name)){
            return JSON.parseArray(cookie_cartList,Cart.class);
        }else {
            //登录从Redis中取

            List<Cart> redis_cartList = cartService.findCartListFromRedis(name);
            if(redis_cartList==null){
                redis_cartList = new ArrayList<>();
            }
            //开始合并cookie
            List<Cart> cartList = cartService.mergeList(redis_cartList, JSON.parseArray(cookie_cartList, Cart.class));

            //存储Redis
            cartService.saveCartListToRedis(name,cartList);
            //清空cookie
            CookieUtil.deleteCookie(request,response,"cartList");

            return redis_cartList;
        }


    }
    @RequestMapping("addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num){

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9009");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            //取回购物车原有信息
            List<Cart> cartList = findCartList();
            //往购物车里面加商品
            cartList = cartService.addGoodsToCartList(cartList,itemId,num);
            //没登录
            if("anonymousUser".equals(name)){
                //添加到cookie中
                CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600 * 24 * 7,"utf-8");
            }else {
                //登录 添加到Redis
                cartService.saveCartListToRedis(name,cartList);
            }
            return new Result(true,"添加购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false,e.getMessage());
        }
    }
}
