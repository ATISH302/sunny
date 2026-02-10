package com.example.sunny.controller;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.sunny.entity.Favorite;
import com.example.sunny.entity.Order;
import com.example.sunny.entity.User;
import com.example.sunny.repository.FavoriteRepository;
import com.example.sunny.repository.OrderRepository;
import com.example.sunny.repository.UserRepository;

// =====================
// マイページ機能：注文履歴・注文詳細表示
// =====================
//
// この Controller は、ログイン中ユーザー専用の
// 「マイページ」機能を担当する。
// 
// 主な役割：
// ・自分の注文履歴一覧を表示
// ・注文の詳細情報を表示
// ・他人の注文は閲覧できないように制御
//
// Spring Security により、
// ログイン済みユーザーのみアクセス可能。
//

@Controller
public class MyPageController {

	private final UserRepository userRepository;
	private final OrderRepository orderRepository;
	private final FavoriteRepository favoriteRepository;

	// Repository を DI（依存性注入）
	public MyPageController(
			UserRepository userRepository,
			OrderRepository orderRepository,
			FavoriteRepository favoriteRepository) {
		this.userRepository = userRepository;
		this.orderRepository = orderRepository;
		this.favoriteRepository = favoriteRepository;
	}

	// =====================
	// マイページ：注文履歴一覧
	// =====================
	//
	// 機能概要：
	// ・ログイン中ユーザーの注文履歴を一覧表示する
	// ・注文は作成日時の降順（新しい順）で表示
	// ・同時に「お気に入り一覧」も取得して画面に渡す
	//
	// 使用画面：
	// ・mypage_orders.html
	//
	@GetMapping("/mypage/orders")
	public String showMyOrders(
			@AuthenticationPrincipal UserDetails loginUser,
			Model model) {

		// Spring Security のログインIDは email 想定
		User user = userRepository.findByEmail(loginUser.getUsername())
				.orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません email=" + loginUser.getUsername()));

		// 注文履歴を新しい順で取得
		List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);

		// ★お気に入り一覧も取得（マイページ表示用）
		List<Favorite> favorites = favoriteRepository.findByUserOrderByCreatedAtDesc(user);

		// 画面に渡すデータ
		model.addAttribute("user", user);
		model.addAttribute("orders", orders);
		model.addAttribute("favorites", favorites);

		return "mypage_orders";
	}

	// =====================
	// マイページ：注文詳細
	// =====================
	//
	// 機能概要：
	// ・指定された注文IDの詳細を表示する
	// ・ログインユーザー本人の注文かどうかをチェック
	// ・他人の注文IDを指定された場合はエラーにする
	//
	// セキュリティ対策：
	// ・URLを直接書き換えても他人の注文は見られない
	//
	// 使用画面：
	// ・mypage_order_detail.html
	//
	@GetMapping("/mypage/orders/{orderId}")
	public String showMyOrderDetail(
			@PathVariable Long orderId,
			@AuthenticationPrincipal UserDetails loginUser,
			Model model) {

		// 注文を取得
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("注文が見つかりません id=" + orderId));

		// ログインユーザーを取得（email 前提）
		User login = userRepository.findByEmail(loginUser.getUsername())
				.orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません email=" + loginUser.getUsername()));

		// ★自分の注文でなければ閲覧不可
		if (!order.getUser().getId().equals(login.getId())) {
			throw new AccessDeniedException("他人の注文は閲覧できません");
		}

		// 画面に渡すデータ
		model.addAttribute("user", order.getUser());
		model.addAttribute("order", order);

		return "mypage_order_detail";
	}
}
