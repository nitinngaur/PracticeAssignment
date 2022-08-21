package com.test.Trade.task;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.test.Trade.service.TradeService;

@Component	
public class TradeScheduler {
	
	@Autowired
	TradeService service;

	@PostConstruct
	@Scheduled(cron = "${trade.schedule}")
	public void updateJob() {
		System.out.println("Updating Job started at :: " + System.currentTimeMillis());
		service.updateExpiryDate();
	}
	
}
