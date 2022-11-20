package com.ifans.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 验证启动程序
 *
 * @author HuangJX
 */
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.ifans")
@SpringBootApplication(scanBasePackages = {"com.ifans"})
public class IfansAuthApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(IfansAuthApplication.class, args);
            System.out.println("ifans验证启动成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}