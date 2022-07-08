package com.whatsapp.messages;

import java.util.Timer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.whatsapp.connections.ConnectionPooling;
import com.whatsapp.sendmessage.SendWhatsAppMessage;
import com.whatsapp.utils.PropertiesUtil;

@SpringBootApplication
@ComponentScan(basePackages = { "com.whatsapp" })

public class WhatsAppMessageServiceApplication {
	ConnectionPooling cp = new ConnectionPooling();

	public static void main(String[] args) {
		SpringApplication.run(WhatsAppMessageServiceApplication.class, args);
	}

}
