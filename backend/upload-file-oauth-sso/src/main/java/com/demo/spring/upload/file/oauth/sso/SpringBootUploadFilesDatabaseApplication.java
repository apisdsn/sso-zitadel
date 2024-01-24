package com.demo.spring.upload.file.oauth.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class SpringBootUploadFilesDatabaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootUploadFilesDatabaseApplication.class, args);
	}
}
