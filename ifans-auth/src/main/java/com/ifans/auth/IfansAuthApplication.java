package com.ifans.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 验证启动程序
 *
 * @author HuangJX
 */
@SpringBootApplication
public class IfansAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(IfansAuthApplication.class, args);
        System.out.println("ifans验证启动成功");
    }
}