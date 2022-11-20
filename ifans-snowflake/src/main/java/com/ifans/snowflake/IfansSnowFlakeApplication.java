package com.ifans.snowflake;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 雪花ID启动程序
 *
 * @author HuangJX
 */
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.ifans")
@SpringBootApplication
public class IfansSnowFlakeApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(IfansSnowFlakeApplication.class, args);
            System.out.println("ifans雪花ID服务启动成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}