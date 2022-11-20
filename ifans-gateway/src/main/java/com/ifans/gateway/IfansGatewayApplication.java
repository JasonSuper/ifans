package com.ifans.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关启动程序
 *
 * @author HuangJX
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.ifans"})
public class IfansGatewayApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(IfansGatewayApplication.class, args);
            System.out.println("ifans网关启动成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}