package com.ifans.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 订单服务启动程序
 *
 * @author HuangJX
 */
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.ifans")
@MapperScan(value = {"com.ifans.order.**.mapper*"})
@SpringBootApplication(scanBasePackages = {"com.ifans"})
public class IfansOrderApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(IfansOrderApplication.class, args);
            System.out.println("ifans订单系统启动成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}