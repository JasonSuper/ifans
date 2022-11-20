package com.ifans.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 系统启动程序
 *
 * @author HuangJX
 */
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.ifans")
@MapperScan(value = {"com.ifans.system.**.mapper*"})
@SpringBootApplication(scanBasePackages = {"com.ifans"})
public class IfansSystemApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(IfansSystemApplication.class, args);
            System.out.println("ifans系统启动成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}