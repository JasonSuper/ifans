package com.ifans.auth;

import com.ifans.common.feign.annotation.EnableIfansFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 验证启动程序
 *
 * @author HuangJX
 */

@EnableIfansFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.ifans"}, exclude = {DataSourceAutoConfiguration.class})
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