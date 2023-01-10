package com.ifans.activiti;

import com.ifans.common.feign.annotation.EnableIfansFeignClients;
import com.ifans.common.security.annotation.EnableIfansResourceServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableDiscoveryClient
@EnableIfansResourceServer
@EnableIfansFeignClients
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@MapperScan(value = {"com.ifans.activiti.**.mapper*"})
@SpringBootApplication(scanBasePackages = {"com.ifans"})
public class IfansActivitiApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(IfansActivitiApplication.class, args);
            System.out.println("ifans工作流启动成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}