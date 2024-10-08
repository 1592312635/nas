package com.minyan.nascapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableEurekaClient
@EnableAsync
@EnableCaching
@EnableAutoConfiguration
@MapperScan(basePackages = "com.minyan.nasdao")
public class NasCapiApplication {

  public static void main(String[] args) {
    SpringApplication.run(NasCapiApplication.class, args);
  }
}
