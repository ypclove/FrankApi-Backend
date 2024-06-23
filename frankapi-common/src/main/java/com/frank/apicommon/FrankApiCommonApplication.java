package com.frank.apicommon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Frank
 * @date 2024/6/23
 */
@SpringBootApplication
@ComponentScan("com.frank")
public class FrankApiCommonApplication {
    public static void main(String[] args) {
        SpringApplication.run(FrankApiCommonApplication.class, args);
    }
}
