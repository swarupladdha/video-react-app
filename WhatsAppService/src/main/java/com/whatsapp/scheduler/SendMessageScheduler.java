package com.whatsapp.scheduler;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.whatsapp.sendmessage.SendWhatsAppMessage;
import com.whatsapp.utils.PropertiesUtil;

@ComponentScan(basePackages = { "com.whatsapp" })
@EnableScheduling
@Component
public class SendMessageScheduler {
	public SendMessageScheduler() {

	}

	@Scheduled(fixedRate = 600000)
	public String sendMessage() {
		int numberOfThread = Integer.parseInt(PropertiesUtil.getProperty("NUMBER_OF_THREADS"));
		for (int i = 0; i < numberOfThread; i++) {
			SendWhatsAppMessage sm = new SendWhatsAppMessage(i);
			Thread t = new Thread(sm);
			t.start();
		}
		return null;

	}
}
