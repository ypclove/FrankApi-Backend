package com.frank.apicommon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 接口文档配置
 * Knife4j 的文档地址： http://ip:port/doc.html
 * swagger 的文档地址：http://ip:port/swagger-ui.html
 *
 * @author Frank
 * @date 2024/06/22
 */
@Configuration
@EnableSwagger2
@Profile({"dev", "prod"})
public class Knife4jConfig {

    @Bean
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 指定 Controller 扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.frank.apibackstage.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 自定义接口文档信息
     *
     * @return ApiInfoBuilder
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // 接口文档的标题
                .title("FrankApi接口开放平台")
                // 接口文档的描述信息
                .description("FrankApi接口平台是一种支持API全生命周期管理的平台，从API的设计、开发、测试、部署、监控到文档生成，提供了一系列工具和服务")
                // 提供服务者
                .termsOfServiceUrl("https://github.com/ypclove")
                .contact(new Contact("Frank", "https://blog.franksteven.me/", "franklove521126@163.com"))
                // 版本
                .version("1.0")
                // 构建
                .build();
    }
}
