package com.offcn.pay.service;

import java.util.Map;

/**
 * All rights Reserved, Designed By www.info4z.club
 * <p>title:com.offcn.pay.service</p>
 * <p>ClassName:AliPayService</p>
 * <p>Description:TODO(请用一句话描述这个类的作用)</p>
 * <p>Compony:Info4z</p>
 * author:poker_heart
 * date:2019/12/9
 * version:1.0
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
public interface AliPayService {
    /**
     * 生成支付宝支付二维码
     *
     * @param out_trade_no
     *            订单号
     * @param total_fee
     *            金额
     * @return
     */
    public Map createNative(String out_trade_no, String total_fee);
    /**
     * 查询支付状态
     * @param out_trade_no
     */
    public Map queryPayStatus(String out_trade_no);
}
