package com.ifans.rank;

import com.ifans.common.feign.annotation.EnableIfansFeignClients;
import com.ifans.common.security.annotation.EnableIfansResourceServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 排行榜服务启动程序
 *
 * @author HuangJX
 */
@EnableDiscoveryClient
@EnableIfansResourceServer
@EnableIfansFeignClients
@EnableFeignClients(basePackages = "com.ifans")
@MapperScan(value = {"com.ifans.rank.**.mapper*"})
@SpringBootApplication(scanBasePackages = {"com.ifans"})
public class IfansRankApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(IfansRankApplication.class, args);
            System.out.println("ifans排行榜启动成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}