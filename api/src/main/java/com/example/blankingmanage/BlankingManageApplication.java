package com.example.blankingmanage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.blankingmanage.mapper")
public class BlankingManageApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlankingManageApplication.class, args);
		System.out.println(">>> 应用启动成功！Mapper 雷达已开启。");
	}

}
