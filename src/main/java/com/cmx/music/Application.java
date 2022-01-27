package com.cmx.music;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cmx.music.dao")
public class Application {

    public static void main(String[] args) {
        String env = System.getProperty("spring.profiles.active");
        if (StringUtils.isBlank(env)) {
            env = "local";
            System.setProperty("spring.profiles.active", env);
        }
        SpringApplication.run(Application.class, args);
    }

}

