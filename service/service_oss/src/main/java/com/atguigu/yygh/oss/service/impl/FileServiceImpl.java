package com.atguigu.yygh.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.oss.service.FileService;
import com.atguigu.yygh.oss.utils.ConstantOssPropertiesUtils;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public String upload(MultipartFile file) {
        String endpoint = ConstantOssPropertiesUtils.EDNPOINT;
        String accessKeyId = ConstantOssPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstantOssPropertiesUtils.SECRECT;
        String bucket = ConstantOssPropertiesUtils.BUCKET;
        try {
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            //上传文件流
            InputStream inputStream = file.getInputStream();
            String filename = file.getOriginalFilename();
            //生成随机唯一值，使用uuid，添加到文件名称里
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            filename = uuid + filename;
            //按照当前日期，创建文件夹，上传到创建文件夹里面
            String timeUrl = new DateTime().toString("yyyy/MM/dd");
            filename = timeUrl + "/" + filename;
            //调用方法实现上传
            ossClient.putObject(bucket, filename, inputStream);
            //关闭ossclient
            ossClient.shutdown();
            //上传之后文件路径
            return "https://" + bucket + "." + endpoint + "/" + filename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
