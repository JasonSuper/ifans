package com.ifans.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 系统启动程序
 *
 * @author HuangJX
 */
@SpringBootApplication
public class IfansSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(IfansSystemApplication.class, args);
        System.out.println("ifans系统启动成功");
    }
}