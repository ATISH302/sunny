//ReservationController

package com.example.sunny.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.security.access.AccessDeniedException;
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

import com.example.sunny.entity.Item;
import com.example.sunny.entity.Order;
import com.example.sunny.entity.User;
import com.example.sunny.repository.ItemRepository;
import com.example.sunny.repository.OrderRepository;
import com.example.sunny.repository.UserRepository;

// =====================
// 注文機能（予約/購入）
// =====================
//
// できること：
// ・注文確認（数量チェック、合計金額計算）
// ・注文確定（注文保存、在庫減算、在庫0ならSOLD）
// ・注文詳細表示（本人チェック）
// ・キャンセル確認/確定（本人チェック、在庫戻し、注文ステータス更新）
//
// ポイント：
// ・ログインIDは email 想定なので findByEmail() を使う
// ・DB更新がある処理は @Transactional を付けている
//

@Controller
public class OrderController {

	private final ItemRepository itemRepository;
	private final UserRepository userRepository;
	private final OrderRepository orderRepository;

	public OrderController(
			ItemRepository itemRepository,
			UserRepository userRepository,
			OrderRepository orderRepository) {
		this.itemRepository = itemRepository;
		this.userRepository = userRepository;
		this.orderRepository = orderRepository;
	}

	// =====================
	// 注文確認ページ
	// =====================
	//
	// ・数量が 1〜在庫数 の範囲内なら確認画面へ
	// ・範囲外なら商品詳細へ戻す
	//
	@GetMapping("/orders/confirm/{itemId}")
	public String showConfirm(
			@PathVariable Long itemId,
			@RequestParam("quantity") int quantity,
			@AuthenticationPrincipal UserDetails loginUser,
			Model model) {

		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new IllegalArgumentException("商品が見つかりません id=" + itemId));

		// 数量チェック
		if (quantity <= 0 || quantity > item.getStock()) {
			return "redirect:/items/" + itemId;
		}

		// 合計金額
		BigDecimal totalPrice = item.getPrice().multiply(BigDecimal.valueOf(quantity));

		model.addAttribute("item", item);
		model.addAttribute("quantity", quantity);
		model.addAttribute("totalPrice", totalPrice);

		// ログインID（email想定）
		model.addAttribute("username", loginUser.getUsername());

		return "order_confirm";
	}

	// =====================
	// 注文確定処理
	// =====================
	//
	// ・注文(Order)を保存
	// ・在庫を減らす
	// ・在庫が0以下になったら商品ステータスを SOLD にする
	//
	@PostMapping("/orders/complete")
	@Transactional
	public String completeOrder(
			@RequestParam("itemId") Long itemId,
			@RequestParam("quantity") int quantity,
			@AuthenticationPrincipal UserDetails loginUser,
			Model model) {

		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new IllegalArgumentException("商品が見つかりません id=" + itemId));

		// ログインユーザー取得（email）
		User user = userRepository.findByEmail(loginUser.getUsername())
				.orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません email=" + loginUser.getUsername()));

		// 数量チェック
		if (quantity <= 0 || quantity > item.getStock()) {
			return "redirect:/items/" + itemId;
		}

		BigDecimal totalPrice = item.getPrice().multiply(BigDecimal.valueOf(quantity));

		// 注文を作成して保存
		Order order = new Order();
		order.setUser(user);
		order.setItem(item);
		order.setQuantity(quantity);
		order.setTotalPrice(totalPrice.intValue());
		order.setStatus("CREATED");
		order.setCreatedAt(LocalDateTime.now());
		orderRepository.save(order);

		// 在庫減算
		item.setStock(item.getStock() - quantity);

		// 在庫0なら SOLD
		if (item.getStock() <= 0) {
			item.setStatus("SOLD");
		}
		itemRepository.save(item);

		model.addAttribute("item", item);
		model.addAttribute("quantity", quantity);
		model.addAttribute("totalPrice", totalPrice);

		return "order_complete";
	}

	// =====================
	// 注文詳細（本人チェック）
	// =====================
	@GetMapping("/orders/detail/{orderId}")
	public String orderDetail(
			@PathVariable Long orderId,
			@AuthenticationPrincipal UserDetails loginUser,
			Model model) {

		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("注文が見つかりません id=" + orderId));

		// 本人の注文かチェック（ログインIDは email）
		if (!order.getUser().getEmail().equals(loginUser.getUsername())) {
			throw new AccessDeniedException("あなたの注文ではありません");
		}

		model.addAttribute("order", order);
		return "order_detail";
	}

	// =====================
	// キャンセル確認ページ
	// =====================
	@GetMapping("/mypage/orders/cancel/confirm/{orderId}")
	public String cancelConfirm(
			@PathVariable Long orderId,
			@AuthenticationPrincipal UserDetails loginUser,
			Model model) {

		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("注文が見つかりません id=" + orderId));

		if (!order.getUser().getEmail().equals(loginUser.getUsername())) {
			throw new AccessDeniedException("他人の注文は操作できません");
		}

		model.addAttribute("order", order);
		return "order_cancel_confirm";
	}

	// =====================
	// キャンセル確定処理
	// =====================
	//
	// ・CREATED の注文だけキャンセル可能
	// ・在庫を戻す
	// ・注文ステータスを CANCELLED にする
	//
	@PostMapping("/mypage/orders/cancel/{orderId}")
	@Transactional
	public String cancelMyOrder(
			@PathVariable Long orderId,
			@AuthenticationPrincipal UserDetails loginUser,
			RedirectAttributes redirectAttributes) {

		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("注文が見つかりません id=" + orderId));

		if (!order.getUser().getEmail().equals(loginUser.getUsername())) {
			throw new AccessDeniedException("他人の注文はキャンセルできません");
		}

		// CREATED 以外はキャンセル不可
		if (!"CREATED".equals(order.getStatus())) {
			return "redirect:/mypage/orders";
		}

		// 在庫を戻す
		Item item = order.getItem();
		item.setStock(item.getStock() + order.getQuantity());

		// SOLD だった商品を販売に戻す（※ここはあなたの設計次第）
		if ("SOLD".equals(item.getStatus()) && item.getStock() > 0) {
			item.setStatus("販売中");
		}

		// 注文をキャンセル状態にする
		order.setStatus("CANCELLED");

		itemRepository.save(item);
		orderRepository.save(order);

		// 成功メッセージ（IDを渡して表示できる）
		redirectAttributes.addFlashAttribute("successMessage", orderId);

		return "redirect:/mypage/orders";
	}
}
