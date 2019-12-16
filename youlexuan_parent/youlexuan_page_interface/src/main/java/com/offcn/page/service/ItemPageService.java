package com.offcn.page.service;

/**
 * All rights Reserved, Designed By www.info4z.club
 * <p>title:com.offcn.page.service</p>
 * <p>ClassName:ItemPageService</p>
 * <p>Description:TODO(请用一句话描述这个类的作用)</p>
 * <p>Compony:Info4z</p>
 * author:poker_heart
 * date:2019/11/29
 * version:1.0
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
public interface ItemPageService {
    //生成静态页面
    public boolean genItemHtml(Long goodsId);
    //删除静态页面
    public boolean deleteItemHtml(Long[] ids);
}
