package com.bjksrs.crawler_sends;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.bjksrs.crawler_sends.mapper")
@EnableScheduling
public class CrawlerSendsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlerSendsApplication.class, args);
    }
}
