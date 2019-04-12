package org.linlinjava.litemall.db;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.linlinjava.litemall.db"})
@MapperScan("org.linlinjava.litemall.db.dao")
public class DBApplication {

    public static void main(String[] args) {
        SpringApplication.run(DBApplication.class, args);
    }

}