package com.ifans.order;

import com.ifans.common.feign.annotation.EnableIfansFeignClients;
import com.ifans.common.security.annotation.EnableIfansResourceServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 订单服务启动程序
 *
 * @author HuangJX
 */
@EnableDiscoveryClient
@EnableIfansResourceServer
@EnableIfansFeignClients
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
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