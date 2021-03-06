package com.offcn.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.mapper.TbUserMapper;
import com.offcn.pojo.TbUser;
import com.offcn.pojo.TbUserExample;
import com.offcn.pojo.TbUserExample.Criteria;
import com.offcn.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * user服务实现层
 * @author senqi
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private JmsTemplate jmsTemplate;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbUser> findAll() {
		return userMapper.selectByExample(null);
	}

	/**
	 * 分页
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbUser> page = (Page<TbUser>) userMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbUser user) {
		userMapper.insert(user);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbUser user){
		userMapper.updateByPrimaryKey(user);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbUser findOne(Long id){
		return userMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			userMapper.deleteByPrimaryKey(id);
		}		
	}
	
	/**
	 * 分页+查询
	 */
	@Override
	public PageResult findPage(TbUser user, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbUserExample example=new TbUserExample();
		Criteria criteria = example.createCriteria();
		
		if(user != null){			
						if(user.getUsername() != null && user.getUsername().length() > 0){
				criteria.andUsernameLike("%" + user.getUsername() + "%");
			}			if(user.getPassword() != null && user.getPassword().length() > 0){
				criteria.andPasswordLike("%" + user.getPassword() + "%");
			}			if(user.getPhone() != null && user.getPhone().length() > 0){
				criteria.andPhoneLike("%" + user.getPhone() + "%");
			}			if(user.getEmail() != null && user.getEmail().length() > 0){
				criteria.andEmailLike("%" + user.getEmail() + "%");
			}			if(user.getSourceType() != null && user.getSourceType().length() > 0){
				criteria.andSourceTypeLike("%" + user.getSourceType() + "%");
			}			if(user.getNickName() != null && user.getNickName().length() > 0){
				criteria.andNickNameLike("%" + user.getNickName() + "%");
			}			if(user.getName() != null && user.getName().length() > 0){
				criteria.andNameLike("%" + user.getName() + "%");
			}			if(user.getStatus() != null && user.getStatus().length() > 0){
				criteria.andStatusLike("%" + user.getStatus() + "%");
			}			if(user.getHeadPic() != null && user.getHeadPic().length() > 0){
				criteria.andHeadPicLike("%" + user.getHeadPic() + "%");
			}			if(user.getQq() != null && user.getQq().length() > 0){
				criteria.andQqLike("%" + user.getQq() + "%");
			}			if(user.getIsMobileCheck() != null && user.getIsMobileCheck().length() > 0){
				criteria.andIsMobileCheckLike("%" + user.getIsMobileCheck() + "%");
			}			if(user.getIsEmailCheck() != null && user.getIsEmailCheck().length() > 0){
				criteria.andIsEmailCheckLike("%" + user.getIsEmailCheck() + "%");
			}			if(user.getSex() != null && user.getSex().length() > 0){
				criteria.andSexLike("%" + user.getSex() + "%");
			}
		}
		
		Page<TbUser> page= (Page<TbUser>)userMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override
    public void sendSms(String phone) {
        //1要获取一个验证码 [100000-999999]
		String code  = (int)(Math.random() * 899999 + 100001)+"";
		//2.存到Redis缓存
		redisTemplate.boundHashOps("phoneCode").put(phone,code);
		//3.发送消息
		Map<String,String> map = new HashMap<>();
		map.put("phone",phone);
		map.put("sign","浪里小白龙");
		map.put("template","SMS_173476140");
		map.put("code","{\"code\":\""+code+"\"}");
		jmsTemplate.convertAndSend("offcn_sms",map);
    }

	@Override
	public boolean checkCode(String phone,String code) {
		String sysCode = (String) redisTemplate.boundHashOps("phoneCode").get(phone);
		if(sysCode==null){
			return false;
		}
		if(!sysCode.equals(code)){
			return false;
		}
		return true;
	}

}
