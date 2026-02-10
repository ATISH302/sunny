//DashboardController.java

package com.example.sunny.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.sunny.entity.Item;
import com.example.sunny.entity.User;
import com.example.sunny.repository.ItemRepository;
import com.example.sunny.repository.UserRepository;

// =====================
// トップページ：商品一覧表示機能
// =====================
//
// この Controller は、ログイン後に最初に表示される
// トップページ（/）を担当する。
//
// 主な役割：
// ・商品一覧を取得して画面に表示する
// ・ログイン中ユーザーの名前を取得し、ヘッダー表示用に渡す
//
// 使用画面：
// ・home.html
//

@Controller
public class HomeController {

	private final ItemRepository itemRepository;
	private final UserRepository userRepository;

	// Repository を DI（依存性注入）
	public HomeController(ItemRepository itemRepository, UserRepository userRepository) {
		this.itemRepository = itemRepository;
		this.userRepository = userRepository;
	}

	// =====================
	// トップページ表示
	// =====================
	//
	// URL：
	// ・GET /
	//
	// 機能概要：
	// ・商品一覧を取得して表示
	// ・ログイン中ユーザーがいる場合は、その「名前」を取得して画面に渡す
	//
	@GetMapping("/")
	public String home(
			@AuthenticationPrincipal UserDetails loginUser,
			Model model) {

		// ---------------------
		// 商品一覧を取得
		// ---------------------
		//
		// items テーブルから全商品を取得し、
		// Thymeleaf（home.html）で一覧表示するため model に渡す
		//
		List<Item> items = itemRepository.findAll();
		model.addAttribute("items", items);

		// ---------------------
		// ログイン中ユーザー名の取得
		// ---------------------
		//
		// Spring Security から取得できる loginUser には
		// ログインID（email または name）が入っている
		//
		if (loginUser != null) {

			String username = loginUser.getUsername(); // email or name

			// email → name の順でユーザーを検索
			User user = userRepository.findByEmail(username)
					.orElseGet(() -> userRepository.findByName(username).orElse(null));

			// ユーザーが存在する場合のみ、名前を画面へ渡す
			if (user != null) {
				model.addAttribute("loginUserName", user.getName());
			}
		}

		// home.html を表示
		return "home";
	}
}
