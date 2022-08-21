package com.test.Trade.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.Trade.entity.TradeEntity;

public interface TradeRepository extends JpaRepository<TradeEntity,String> {
}
