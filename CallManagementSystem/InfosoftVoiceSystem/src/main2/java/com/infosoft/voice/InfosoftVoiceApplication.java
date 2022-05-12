package com.infosoft.voice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.infosoft.scheduler.CallScheduler;

@SpringBootApplication
@ComponentScan("com.infosoft")

public class InfosoftVoiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InfosoftVoiceApplication.class, args);
	}

}
