package com.example.sunny.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.sunny.entity.Order;
import com.example.sunny.repository.OrderRepository;

// =====================
// スタッフ専用：注文管理機能
// =====================
//
// この Controller は、スタッフ（STAFF）および管理者（ADMIN）が
// 注文状況を確認・更新するための機能を提供する。
//
// 主な役割：
// ・全注文の一覧表示
// ・注文ステータスの更新（例：CREATED → SHIPPED など）
//
// アクセス制御：
// ・/staff/** は SecurityConfig により STAFF / ADMIN のみアクセス可能
//

@Controller
public class StaffOrderController {

	private final OrderRepository orderRepository;

	// OrderRepository を DI（依存性注入）
	public StaffOrderController(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	// =====================
	// スタッフ：注文管理画面
	// =====================
	//
	// 機能概要：
	// ・すべての注文を作成日時の降順で一覧表示
	// ・最新の注文が上に表示される
	//
	// 使用画面：
	// ・staff_orders.html
	//
	@GetMapping("/staff/orders")
	public String staffOrders(
			@AuthenticationPrincipal UserDetails loginUser,
			Model model) {

		// 全注文を作成日時の降順で取得
		List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();

		// 画面へデータを渡す
		model.addAttribute("orders", orders);

		// ログイン中スタッフのユーザー名（表示用）
		model.addAttribute("username", loginUser.getUsername());

		return "staff_orders";
	}

	// =====================
	// スタッフ：注文ステータス更新
	// =====================
	//
	// 機能概要：
	// ・指定された注文のステータスを更新する
	// ・例：CREATED → PREPARING → SHIPPED → COMPLETED
	//
	// 業務ルール：
	// ・CANCELLED / COMPLETED の注文は変更不可
	// （実務的な制御が入っている点がポイント）
	//
	@PostMapping("/staff/orders/update/{orderId}")
	@Transactional
	public String updateStatus(
			@PathVariable Long orderId,
			@RequestParam("status") String status,
			RedirectAttributes redirectAttributes) {

		// 対象注文を取得
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("注文が見つかりません id=" + orderId));

		// キャンセル済み・完了済みの注文は更新不可
		if ("CANCELLED".equals(order.getStatus())
				|| "CANCELED".equals(order.getStatus())
				|| "COMPLETED".equals(order.getStatus())) {

			redirectAttributes.addFlashAttribute(
					"errorMessage",
					"この注文は更新できません。");

			return "redirect:/staff/orders";
		}

		// ステータス更新
		order.setStatus(status);
		orderRepository.save(order);

		// 完了メッセージを表示
		redirectAttributes.addFlashAttribute(
				"successMessage",
				"注文ID " + orderId + " を更新しました。");

		return "redirect:/staff/orders";
	}
}
