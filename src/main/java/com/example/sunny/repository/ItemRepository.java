package com.example.sunny.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sunny.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

	// 公開中の商品だけ取る用
	List<Item> findByStatus(String status);
}
