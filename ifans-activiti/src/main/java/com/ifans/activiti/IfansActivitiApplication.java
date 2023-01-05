package com.ifans.activiti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
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