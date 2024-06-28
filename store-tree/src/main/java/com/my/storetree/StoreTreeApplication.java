package com.my.storetree;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.my.*.mapper")
public class StoreTreeApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreTreeApplication.class, args);
    }

}
