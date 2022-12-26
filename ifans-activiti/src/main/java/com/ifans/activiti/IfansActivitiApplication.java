package com.ifans.activiti;

import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;

@SpringBootApplication(exclude = {RedissonAutoConfiguration.class, RedisAutoConfiguration.class})
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