package com.offcn.porta.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.content.service.ContentService;
import com.offcn.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * All rights Reserved, Designed By www.info4z.club
 * <p>title:com.offcn.porta.controller</p>
 * <p>ClassName:portaController</p>
 * <p>Description:TODO(请用一句话描述这个类的作用)</p>
 * <p>Compony:Info4z</p>
 * author:poker_heart
 * date:2019/11/25
 * version:1.0
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@RestController
@RequestMapping("/porta")
public class portaController {
    @Reference
    private ContentService contentService;
    @RequestMapping("findContentByCid")
   public List<TbContent> findContentByCid(Long id){
       return contentService.findContentByCid(id);
    }

 }
