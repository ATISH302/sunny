package com.example.sunny.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.sunny.entity.Item;
import com.example.sunny.entity.User;
import com.example.sunny.repository.FavoriteRepository;
import com.example.sunny.repository.ItemRepository;
import com.example.sunny.repository.ReviewRepository;
import com.example.sunny.repository.UserRepository;

// =====================
// 商品詳細画面：表示用コントローラー
// =====================
//
// この Controller は、商品詳細ページの表示を担当する。
//
// 主な役割：
// ・商品情報の取得
// ・ログインユーザーのお気に入り状態の判定
// ・レビュー情報（平均評価・件数・一覧）の取得
//
// 使用画面：
// ・item_detail.html
//

@Controller
public class ItemController {

	private final ItemRepository itemRepository;
	private final UserRepository userRepository;
	private final FavoriteRepository favoriteRepository;
	private final ReviewRepository reviewRepository;

	// Repository を DI（依存性注入）
	public ItemController(
			ItemRepository itemRepository,
			UserRepository userRepository,
			FavoriteRepository favoriteRepository,
			ReviewRepository reviewRepository) {
		this.itemRepository = itemRepository;
		this.userRepository = userRepository;
		this.favoriteRepository = favoriteRepository;
		this.reviewRepository = reviewRepository;
	}

	// =====================
	// 商品詳細ページ表示
	// =====================
	//
	// URL例：
	// /items/1
	//
	// 処理の流れ：
	// 1. 商品IDから商品情報を取得
	// 2. ログイン中ユーザーがいる場合
	//    ・お気に入り登録済みかを判定
	//    ・ユーザーIDを取得
	// 3. レビューの平均評価・件数・一覧を取得
	// 4. 画面（item_detail.html）に必要な情報を渡す
	//
	@GetMapping("/items/{id}")
	public String showItemDetail(
			@PathVariable Long id,
			@AuthenticationPrincipal UserDetails loginUser,
			Model model) {

		// 商品情報を取得（存在しなければエラー）
		Item item = itemRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("商品が見つかりません id=" + id));

		boolean isFavorite = false; // お気に入り登録済みかどうか
		Long loginUserId = null; // ログインユーザーID（未ログイン時は null）

		// ログインしている場合のみユーザー情報を取得
		if (loginUser != null) {
			String username = loginUser.getUsername(); // email or name

			User user = userRepository.findByEmail(username)
					.orElseGet(() -> userRepository.findByName(username).orElse(null));

			if (user != null) {
				// お気に入り登録済みか判定
				isFavorite = favoriteRepository.existsByUserAndItem(user, item);
				loginUserId = user.getId();
			}
		}

		// 商品情報とお気に入り状態を画面に渡す
		model.addAttribute("item", item);
		model.addAttribute("isFavorite", isFavorite);
		model.addAttribute("loginUserId", loginUserId);

		// =====================
		// レビュー情報の取得
		// =====================

		// 平均評価（レビューが無い場合は null になる）
		Double avgRating = reviewRepository.findAverageRatingByItemId(id);

		// レビュー件数
		long reviewCount = reviewRepository.countByItemId(id);

		model.addAttribute("avgRating", avgRating == null ? 0.0 : avgRating);
		model.addAttribute("reviewCount", reviewCount);

		// レビュー一覧（新しい順）
		model.addAttribute(
				"reviews",
				reviewRepository.findByItemOrderByCreatedAtDesc(item));

		return "item_detail";
	}
}
