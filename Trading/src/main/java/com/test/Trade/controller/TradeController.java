package com.test.Trade.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.Trade.entity.TradeEntity;
import com.test.Trade.repository.TradeRepository;
import com.test.Trade.service.TradeService;

@RestController
@RequestMapping("/trade")
public class TradeController {

	@Autowired
	TradeService service;

	@Autowired
	TradeRepository repo;

	@GetMapping
	public List<TradeEntity> getTrade() {
		return repo.findAll();
	}

	@PostMapping
	public ResponseEntity<String> postTrade(@RequestBody TradeEntity trade) {

		if (service.validate(trade)) {
			repo.save(trade);
		} else {
			throw new RuntimeException("Invalid Request");
		}
		return  ResponseEntity.status(HttpStatus.OK).build();
	}

}
