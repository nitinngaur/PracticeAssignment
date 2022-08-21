package com.test.Trade;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;

import org.awaitility.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.test.Trade.controller.TradeController;
import com.test.Trade.entity.TradeEntity;
import com.test.Trade.task.TradeScheduler;

@SpringJUnitConfig(TradeApplication.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TradeTests {

	@SpyBean
	private TradeScheduler scheduler;
	
	@Autowired
	private TradeController tradeController;

	@Test
	public void whenOneMinutePassed() {
		await().atMost(Duration.ONE_MINUTE)
				.untilAsserted(() -> verify(scheduler, atLeast(1)).updateJob());
	}
	
	@Test
	void testTradeValidateAndStore_successful() {
		ResponseEntity responseEntity = tradeController.postTrade((createTrade("T1",1,LocalDate.now())));
		Assertions.assertEquals(ResponseEntity.status(HttpStatus.OK).build(),responseEntity);
		List<TradeEntity> tradeList =tradeController.getTrade();
		Assertions.assertEquals(1, tradeList.size());
		Assertions.assertEquals("T1",tradeList.get(0).getTradeId());
	}

	@Test
	void testTradeValidateAndStoreWhenMaturityDatePast() {
		try {
			LocalDate localDate = getLocalDate(2015, 05, 21);
			ResponseEntity responseEntity = tradeController.postTrade(createTrade("T2", 1, localDate));
		}catch (Exception e) {
			Assertions.assertEquals("Invalid Request", e.getMessage());
		}
	}

	@Test
	void testTradeValidateAndStoreWhenOldVersion() {
		// step-1 create trade
		ResponseEntity responseEntity = tradeController.postTrade(createTrade("T1",2,LocalDate.now()));
		Assertions.assertEquals(ResponseEntity.status(HttpStatus.OK).build(),responseEntity);
		List<TradeEntity> tradeList =tradeController.getTrade();
		Assertions.assertEquals(1, tradeList.size());
		Assertions.assertEquals("T1",tradeList.get(0).getTradeId());
		Assertions.assertEquals(2,tradeList.get(0).getVersion());
		Assertions.assertEquals("T1B1",tradeList.get(0).getBookId());

		//step-2 create trade with old version
		try {
			ResponseEntity responseEntity1 = tradeController.postTrade(createTrade("T1", 1, LocalDate.now()));


		}catch (Exception e){			
			System.out.println(e.getMessage());
		}
		List<TradeEntity> tradeList1 =tradeController.getTrade();
		Assertions.assertEquals(1, tradeList1.size());
		Assertions.assertEquals("T1",tradeList1.get(0).getTradeId());
		Assertions.assertEquals(2,tradeList1.get(0).getVersion());
		Assertions.assertEquals("T1B1",tradeList.get(0).getBookId());
	}

	@Test
	void testTradeValidateAndStoreWhenSameVersionTrade(){
		ResponseEntity responseEntity = tradeController.postTrade(createTrade("T1",2,LocalDate.now()));
		Assertions.assertEquals(ResponseEntity.status(HttpStatus.OK).build(),responseEntity);
		List<TradeEntity> tradeList =tradeController.getTrade();
		Assertions.assertEquals(1, tradeList.size());
		Assertions.assertEquals("T1",tradeList.get(0).getTradeId());
		Assertions.assertEquals(2,tradeList.get(0).getVersion());
		Assertions.assertEquals("T1B1",tradeList.get(0).getBookId());

		//step-2 create trade with same version
		TradeEntity trade2 = createTrade("T1",2,LocalDate.now());
		trade2.setBookId("T1B1V2");
		ResponseEntity responseEntity2 = tradeController.postTrade(trade2);
		Assertions.assertEquals(ResponseEntity.status(HttpStatus.OK).build(),responseEntity2);
		List<TradeEntity> tradeList2 =tradeController.getTrade();
		Assertions.assertEquals(1, tradeList2.size());
		Assertions.assertEquals("T1",tradeList2.get(0).getTradeId());
		Assertions.assertEquals(2,tradeList2.get(0).getVersion());
		Assertions.assertEquals("T1B1V2",tradeList2.get(0).getBookId());

		//step-2 create trade with new version
		TradeEntity trade3 = createTrade("T1",2,LocalDate.now());
		trade3.setBookId("T1B1V3");
		ResponseEntity responseEntity3 = tradeController.postTrade(trade3);
		Assertions.assertEquals(ResponseEntity.status(HttpStatus.OK).build(),responseEntity3);
		List<TradeEntity> tradeList3 =tradeController.getTrade();
		Assertions.assertEquals(1, tradeList3.size());
		Assertions.assertEquals("T1",tradeList3.get(0).getTradeId());
		Assertions.assertEquals(2,tradeList3.get(0).getVersion());
		Assertions.assertEquals("T1B1V3",tradeList3.get(0).getBookId());

	}
	private TradeEntity createTrade(String tradeId,int version,LocalDate  maturityDate){
		TradeEntity trade = new TradeEntity();
		trade.setTradeId(tradeId);
		trade.setBookId(tradeId+"B1");
		trade.setVersion(version);
		trade.setCounterPartyId(tradeId+"Cpty");
		trade.setMaturityDate(maturityDate);
		trade.setExpired("Y");
		return trade;
	}

	public static LocalDate getLocalDate(int year,int month, int day){
		LocalDate localDate = LocalDate.of(year,month,day);
		return localDate;
	}
	
	

}
