package com.amgreat.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan (basePackages = "com.amgreat.lib, com.amgreat.job.be, com.amgreat.job.cache, com.amgreat.job.ctrl, com.amgreat.job.data, com.amgreat.job.util, com.amgreat.vo, com.amgreat.job.html")
@SpringBootApplication
public class MainRuntime 
{
    public static void main(String[] args) {
		SpringApplication.run(MainRuntime.class, args);
	}
}
