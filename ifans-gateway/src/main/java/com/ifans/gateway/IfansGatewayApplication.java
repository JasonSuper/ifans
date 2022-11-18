package com.ifans.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 网关启动程序
 *
 * @author HuangJX
 */
@SpringBootApplication
public class IfansGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(IfansGatewayApplication.class, args);
        System.out.println("ifans网关启动成功");
    }
}