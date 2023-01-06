package com.ifans.fileupload;

import com.ifans.common.feign.annotation.EnableIfansFeignClients;
import com.ifans.common.security.annotation.EnableIfansResourceServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 文件上传
 */
@EnableDiscoveryClient
@EnableIfansResourceServer
@EnableIfansFeignClients
@MapperScan(value = {"com.ifans.fileupload.**.mapper*"})
@SpringBootApplication(scanBasePackages = {"com.ifans"})
public class IfansFileUploadApplication {
    public static void main(String[] args) {
        SpringApplication.run(IfansFileUploadApplication.class, args);
        System.out.println("ifans文件上传模块启动成功");
    }
}
