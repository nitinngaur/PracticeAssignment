package com.test.Trade.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.Trade.entity.TradeEntity;
import com.test.Trade.repository.TradeRepository;

@Service
public class TradeService {

	@Autowired
	TradeRepository repo;

	// Update Expiry Date
	public void updateExpiryDate() {
		repo.findAll().stream().forEach(t -> {
			if (!validateMaturityDate(t)) {
				t.setExpired("Y");
				System.out.println("Expiry data update for trade => " + t.toString());
				repo.save(t);
			}
		});
	}

	private boolean validateMaturityDate(TradeEntity trade) {
		return trade.getMaturityDate().isBefore(LocalDate.now()) ? false : true;
	}

	// validate
	public boolean validate(TradeEntity trade) {
		if (validateMaturityDate(trade)) {
			Optional<TradeEntity> exsitingTrade = repo.findById(trade.getTradeId());
			if (exsitingTrade.isPresent()) {
				return validateVersion(trade, exsitingTrade.get());
			} else {
				return true;
			}
		}
		return false;
	}

	private boolean validateVersion(TradeEntity newtrade, TradeEntity oldTrade) {
		if (newtrade.getVersion() >= oldTrade.getVersion()) {
			return true;
		}
		return false;
	}

}
