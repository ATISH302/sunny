package com.example.sunny.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sunny.entity.Order;
import com.example.sunny.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long> {

	// ログインユーザーの注文履歴を新しい順に取得
	List<Order> findByUserOrderByCreatedAtDesc(User user);

	// ★追加：全注文を新しい順で表示（スタッフ用）
	List<Order> findAllByOrderByCreatedAtDesc();
}