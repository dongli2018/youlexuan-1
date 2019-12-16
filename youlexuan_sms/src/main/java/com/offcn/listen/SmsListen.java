package com.offcn.listen;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.offcn.utils.SmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * All rights Reserved, Designed By www.info4z.club
 * <p>title:com.offcn.listen</p>
 * <p>ClassName:SmsListen</p>
 * <p>Description:TODO(请用一句话描述这个类的作用)</p>
 * <p>Compony:Info4z</p>
 * author:poker_heart
 * date:2019/12/3
 * version:1.0
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Component
public class SmsListen {
    @Autowired
    private SmsUtils smsUtils;
    @JmsListener(destination = "offcn_sms")
    public void send(Map<String,String> map){
        try {
            SendSmsResponse smsResponse = smsUtils.sendSms(map.get("phone"), map.get("sign"), map.get("template"), map.get("code"));
            System.out.println("发送消息状态;"+smsResponse.getCode());
            System.out.println("回复消息:"+smsResponse.getMessage());
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
