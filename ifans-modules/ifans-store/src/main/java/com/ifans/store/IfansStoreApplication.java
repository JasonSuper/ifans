package com.ifans.store;

import com.ifans.common.feign.annotation.EnableIfansFeignClients;
import com.ifans.common.security.annotation.EnableIfansResourceServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 道具服务启动程序
 *
 * @author HuangJX
 */
@EnableDiscoveryClient
@EnableIfansResourceServer
@EnableIfansFeignClients
@MapperScan(value = {"com.ifans.store.**.mapper*"})
@SpringBootApplication(scanBasePackages = {"com.ifans"})
public class IfansStoreApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(IfansStoreApplication.class, args);
            System.out.println("ifans系统启动成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}