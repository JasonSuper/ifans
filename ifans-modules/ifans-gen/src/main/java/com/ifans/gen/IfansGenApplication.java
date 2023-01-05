package com.ifans.gen;

import com.ifans.common.feign.annotation.EnableIfansFeignClients;
import com.ifans.common.security.annotation.EnableIfansResourceServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 代码生成
 *
 * @author ruoyi
 */
@EnableDiscoveryClient
@EnableIfansResourceServer
@EnableIfansFeignClients
@MapperScan(value = {"com.ifans.gen.**.mapper*"})
@SpringBootApplication(scanBasePackages = {"com.ifans"})
public class IfansGenApplication {
    public static void main(String[] args) {
        SpringApplication.run(IfansGenApplication.class, args);
        System.out.println("ifans 代码生成模块启动成功");
    }
}
