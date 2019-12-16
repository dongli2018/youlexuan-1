package com.offcn.sellergoods.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.pojo.TbBrand;
import com.offcn.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.info4z.club
 * <p>title:com.offcn.sellergoods.controller</p>
 * <p>ClassName:BrandController</p>
 * <p>Description:TODO(请用一句话描述这个类的作用)</p>
 * <p>Compony:Info4z</p>
 * author:poker_heart
 * date:2019/11/14
 * version:1.0
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;
    @RequestMapping("findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }
    @RequestMapping("/findPage")
    public PageResult findPage(int page,int size){
        return brandService.findPage(page, size);
    }
    @RequestMapping("add")
    public Result add(@RequestBody TbBrand brand){
       try {
           brandService.add(brand);
           return new Result(true,"添加成功");
       } catch (Exception e) {
           e.printStackTrace();
           return new Result(false,"添加失败");
       }
    }
    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }
    @RequestMapping("findOne")
    public TbBrand findOne(Long id){
       return brandService.findOne(id);
    }
    @RequestMapping("delete")
    public  Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
    @RequestMapping("search")
    public PageResult search(int page, int size,@RequestBody TbBrand brand){
        return brandService.findPage(page,size,brand);
    }
    @RequestMapping("selectBrand")
    public List<Map> selectBrand(){
        return brandService.selectBrand();
    }
}
