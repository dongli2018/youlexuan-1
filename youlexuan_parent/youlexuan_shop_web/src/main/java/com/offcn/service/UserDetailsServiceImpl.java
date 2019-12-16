package com.offcn.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.pojo.TbSeller;
import com.offcn.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * All rights Reserved, Designed By www.info4z.club
 * <p>title:com.offcn.service</p>
 * <p>ClassName:UserDetailsServiceImpl</p>
 * <p>Description:TODO(请用一句话描述这个类的作用)</p>
 * <p>Compony:Info4z</p>
 * author:poker_heart
 * date:2019/11/19
 * version:1.0
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
public class UserDetailsServiceImpl implements UserDetailsService {
    @Reference
    private SellerService sellerService;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        TbSeller seller = sellerService.findOne(s);
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        if(seller != null){
            //状态通过的
            if(seller.getStatus().equals("1")){
                return  new User(s,seller.getPassword(),list);
            }else {
                return null;
            }
        }else {
            return null;
        }
    }
}
