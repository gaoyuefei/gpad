package com.gpad.gpadtool;

import com.gpad.common.security.annotation.EnableCustomConfig;
import com.gpad.common.security.annotation.EnableRyFeignClients;
import com.gpad.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication
public class GpadToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(GpadToolApplication.class, args);
    }

}
