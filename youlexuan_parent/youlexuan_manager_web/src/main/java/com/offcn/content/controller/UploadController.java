package com.offcn.content.controller;

import com.offcn.entity.Result;
import com.offcn.utils.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * All rights Reserved, Designed By www.info4z.club
 * <p>title:com.offcn.shop.controller</p>
 * <p>ClassName:UploadController</p>
 * <p>Description:TODO(请用一句话描述这个类的作用)</p>
 * <p>Compony:Info4z</p>
 * author:poker_heart
 * date:2019/11/20
 * version:1.0
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@RestController

public class UploadController {
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;
    @RequestMapping("upload")
    public Result upload(MultipartFile file){
        //获取文件名
        String filename = file.getOriginalFilename();
        //获取后缀名
        String lastname = filename.substring(filename.lastIndexOf(".") + 1);
        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fdfs_client.conf");
            String path = fastDFSClient.uploadFile(file.getBytes(), lastname);
            String url = FILE_SERVER_URL + path;
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
