package com.ifans.fileupload.controller;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.ifans.common.core.util.R;
import com.ifans.common.oss.OssService;
import com.ifans.common.security.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * oss文件上传
 */
@RestController
@RequestMapping("/oss")
public class OssFileController {

    @Autowired
    private OssService ossService;

    /**
     * 上传头像到阿里云OSS
     *
     * @param file 头像文件
     * @param path 文件存放路径
     */
    @PostMapping("/simpleUpload")
    public R updateAvatar(MultipartFile file, String path) {
        try {
            //获取上传文件输入流
            InputStream inputStream = file.getInputStream();
            //获取文件名称
            String fileName = file.getOriginalFilename();
            //获取上传的文件  通过 MultipartFile
            String url = ossService.simpleUpload(fileName, path, inputStream);//返回上传图片的路径
            return R.ok(url, "上传成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.failed("修改头像失败");
    }
}
