package com.frank.apibackstage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Frank
 * @data 2024/06/22
 */
// @EnableDubbo
@EnableScheduling
@SpringBootApplication
@ComponentScan("com.frank")
@MapperScan("com.frank.apibackstage.mapper")
public class FrankApiBackstageApplication {
    public static void main(String[] args) {
        SpringApplication.run(FrankApiBackstageApplication.class, args);
    }
}