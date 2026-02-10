package com.example.sunny.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.sunny.entity.Favorite;
import com.example.sunny.entity.Item;
import com.example.sunny.entity.User;
import com.example.sunny.repository.FavoriteRepository;
import com.example.sunny.repository.ItemRepository;
import com.example.sunny.repository.UserRepository;

// =====================
// お気に入り機能 Controller
// =====================
//
// この Controller は、ログインユーザーが商品を
// 「お気に入りに追加・削除・一覧表示」するための機能を提供する。
//
// 主な役割：
// ・商品詳細画面からお気に入りに追加
// ・お気に入りから削除
// ・マイページでお気に入り一覧を表示
//
// 特徴：
// ・ログインユーザーは Spring Security から取得
// ・お気に入りの重複登録を防止
// ・処理後は RedirectAttributes でメッセージを表示
//

@Controller
public class FavoriteController {

	private final FavoriteRepository favoriteRepository;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;

	// Repository を DI（依存性注入）
	public FavoriteController(
			FavoriteRepository favoriteRepository,
			UserRepository userRepository,
			ItemRepository itemRepository) {
		this.favoriteRepository = favoriteRepository;
		this.userRepository = userRepository;
		this.itemRepository = itemRepository;
	}

	// =====================
	// お気に入り追加
	// =====================
	//
	// 機能概要：
	// ・商品詳細画面から商品をお気に入りに追加する
	// ・すでに登録済みの場合は何もしない
	//
	// URL例：
	// POST /favorites/add/{itemId}
	//
	// 処理の流れ：
	// 1. ログイン中ユーザーを取得（email で検索）
	// 2. 対象の商品を取得
	// 3. すでにお気に入り登録済みかチェック
	// 4. 未登録の場合のみ Favorite を作成して保存
	// 5. 商品詳細画面へリダイレクト
	//
	@PostMapping("/favorites/add/{itemId}")
	@Transactional
	public String addFavorite(
			@PathVariable Long itemId,
			@AuthenticationPrincipal UserDetails loginUser,
			RedirectAttributes redirectAttributes) {

		// ログインユーザー取得（loginUser.getUsername() = email）
		User user = userRepository.findByEmail(loginUser.getUsername())
				.orElseThrow(() -> new IllegalArgumentException(
						"ユーザーが見つかりません email=" + loginUser.getUsername()));

		// 対象商品を取得
		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new IllegalArgumentException(
						"商品が見つかりません id=" + itemId));

		// すでに登録済みでなければ追加
		if (!favoriteRepository.existsByUserAndItem(user, item)) {
			Favorite favorite = new Favorite();
			favorite.setUser(user);
			favorite.setItem(item);
			favorite.setCreatedAt(LocalDateTime.now());
			favoriteRepository.save(favorite);
		}

		redirectAttributes.addFlashAttribute(
				"successMessage", "お気に入りに追加しました。");

		return "redirect:/items/" + itemId;
	}

	// =====================
	// お気に入り削除
	// =====================
	//
	// 機能概要：
	// ・お気に入り一覧から商品を削除する
	//
	// URL例：
	// POST /favorites/remove/{itemId}
	//
	// 処理の流れ：
	// 1. ログインユーザーを取得
	// 2. 対象商品を取得
	// 3. 該当する Favorite レコードを削除
	// 4. お気に入り一覧画面へリダイレクト
	//
	@PostMapping("/favorites/remove/{itemId}")
	@Transactional
	public String removeFavorite(
			@PathVariable Long itemId,
			@AuthenticationPrincipal UserDetails loginUser,
			RedirectAttributes redirectAttributes) {

		User user = userRepository.findByEmail(loginUser.getUsername())
				.orElseThrow(() -> new IllegalArgumentException(
						"ユーザーが見つかりません email=" + loginUser.getUsername()));

		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new IllegalArgumentException(
						"商品が見つかりません id=" + itemId));

		favoriteRepository.deleteByUserAndItem(user, item);

		redirectAttributes.addFlashAttribute(
				"successMessage", "お気に入りから削除しました。");

		return "redirect:/mypage/favorites";
	}

	// =====================
	// マイページ：お気に入り一覧表示
	// =====================
	//
	// 機能概要：
	// ・ログインユーザーのお気に入り商品を一覧表示する
	// ・登録日時の新しい順で表示
	//
	// 使用画面：
	// ・mypage_favorites.html
	//
	@GetMapping("/mypage/favorites")
	public String myFavorites(
			@AuthenticationPrincipal UserDetails loginUser,
			Model model) {

		User user = userRepository.findByEmail(loginUser.getUsername())
				.orElseThrow(() -> new IllegalArgumentException(
						"ユーザーが見つかりません email=" + loginUser.getUsername()));

		List<Favorite> favorites = favoriteRepository.findByUserOrderByCreatedAtDesc(user);

		model.addAttribute("user", user);
		model.addAttribute("favorites", favorites);

		return "mypage_favorites";
	}
}
