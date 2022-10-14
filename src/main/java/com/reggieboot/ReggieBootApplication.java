package com.reggieboot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@Slf4j
@SpringBootApplication
@ServletComponentScan
public class ReggieBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieBootApplication.class, args);
        log.info("启动成功");
    }

}
