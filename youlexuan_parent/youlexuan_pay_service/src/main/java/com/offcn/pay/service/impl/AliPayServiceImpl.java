package com.offcn.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.offcn.pay.service.AliPayService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.info4z.club
 * <p>title:com.offcn.pay.service.impl</p>
 * <p>ClassName:AliPayServiceImpl</p>
 * <p>Description:TODO(请用一句话描述这个类的作用)</p>
 * <p>Compony:Info4z</p>
 * author:poker_heart
 * date:2019/12/9
 * version:1.0
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Service
public class AliPayServiceImpl implements AliPayService {
    @Autowired
    private AlipayClient alipayClient;
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        Map<String, String> map = new HashMap<String, String>();
        // 创建预下单请求对象
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        // 设置业务参数
        request.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_fee + "\","
                + "\"subject\":\"优乐选购商城\","
                + "\"timeout_express\":\"60m\"}");
        // 发出预下单业务请求
        try {
            AlipayTradePrecreateResponse response = alipayClient.execute(request);
            // 从相应对象读取相应结果
            String code = response.getCode();
            System.out.println("支付宝接口响应码:" + code);
            // 全部的响应结果
            String body = response.getBody();
            System.out.println("支付宝返回结果:" + body);

            if (code.equals("10000")) {
                map.put("qrcode", response.getQrCode());
                map.put("out_trade_no", response.getOutTradeNo());
                map.put("total_fee", total_fee);
                System.out.println("返回qrcode:" + response.getQrCode());
                System.out.println("返回out_trade_no:" + response.getOutTradeNo());
                System.out.println("返回total_fee:" + total_fee);
            } else {
                System.out.println("预下单接口调用失败:" + body);
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 交易查询接口alipay.trade.query
     * 获取指定订单编号的，交易状态
     *
     */
    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) {
        Map<String, String> map = new HashMap<String, String>();
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        // 设置业务参数
        request.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\"}");

        // 发出请求
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            String code = response.getCode();

            System.out.println("查询交易状态--返回值1:" + code);
            System.out.println("查询交易状态--返回值2:" + response.getBody());

            if (code.equals("10000")) {
                map.put("out_trade_no", out_trade_no);
                map.put("tradestatus", response.getTradeStatus());
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return map;
    }
}

