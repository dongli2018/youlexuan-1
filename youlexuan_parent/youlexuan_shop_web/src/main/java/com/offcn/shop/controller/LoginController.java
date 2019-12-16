package com.offcn.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * All rights Reserved, Designed By www.info4z.club
 * <p>title:com.offcn.sellergoods.controller</p>
 * <p>ClassName:LoginController</p>
 * <p>Description:TODO(请用一句话描述这个类的作用)</p>
 * <p>Compony:Info4z</p>
 * author:poker_heart
 * date:2019/11/19
 * version:1.0
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    @RequestMapping("getName")
    public String getName(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
