package com.ifans.system;

import com.ifans.common.feign.annotation.EnableIfansFeignClients;
import com.ifans.common.security.annotation.EnableIfansResourceServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 系统启动程序
 *
 * @author HuangJX
 */
@EnableIfansResourceServer
@EnableIfansFeignClients
@EnableDiscoveryClient
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
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