package com.offcn.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.pojo.TbUser;
import com.offcn.user.service.UserService;
import com.offcn.utils.PhoneFormatCheckUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * usercontroller
 * @author senqi
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;
	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user,String code){
		try {
			if(!userService.checkCode(user.getPhone(),code)){
				return  new Result(false,"验证码错误");
			}
			//添加时间
			user.setCreated(new Date());
			user.setUpdated(new Date());
			//密码加密
			user.setPassword(DigestUtils.md5Hex(user.getPassword()));

			userService.add(user);
			redisTemplate.boundHashOps("phoneCode").delete(user.getPhone());
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	//发送验证码
	@RequestMapping("/sendSms")
	public Result sendSms(String phone){
		if(!PhoneFormatCheckUtils.isChinaPhoneLegal(phone)){
			return new Result(false,"手机号码不合法");
		}
		try {
			userService.sendSms(phone);
			return new Result(true,"发送成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"发送失败");
		}
	}
	@RequestMapping("getName")
	public String getName(){
		return  SecurityContextHolder.getContext().getAuthentication().getName();
	}
}
