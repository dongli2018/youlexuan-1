package com.offcn.sellergoods.service;

import com.offcn.entity.PageResult;
import com.offcn.pojo.TbBrand;

import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.info4z.club
 * <p>title:com.offcn.sellergoods.service</p>
 * <p>ClassName:BrandService</p>
 * <p>Description:TODO(请用一句话描述这个类的作用)</p>
 * <p>Compony:Info4z</p>
 * author:poker_heart
 * date:2019/11/14
 * version:1.0
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
public interface BrandService {
    List<TbBrand> findAll();
    PageResult findPage(int pagenum,int pagesize);
    PageResult findPage(int pagenum,int pagesize,TbBrand brand);
    void add(TbBrand brand);
    void update(TbBrand brand);
    TbBrand findOne(Long id);
    void delete(Long[] ids);

    List<Map> selectBrand();
}
