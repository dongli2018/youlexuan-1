package com.offcn.search.service;

import com.offcn.pojo.TbItem;

import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.info4z.club
 * <p>title:com.offcn.search.service</p>
 * <p>ClassName:ItemSearchService</p>
 * <p>Description:TODO(请用一句话描述这个类的作用)</p>
 * <p>Compony:Info4z</p>
 * author:poker_heart
 * date:2019/11/26
 * version:1.0
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
public interface ItemSearchService {
    public Map<String,Object> search(Map searchMap);
    //导入数据
    public void importDate(List<TbItem> list);
    //删除数据
    public void deleteDate(Long[] ids);
}
