package com.example.sunny.config;

import java.security.Principal;
import java.util.Optional;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.sunny.entity.User;
import com.example.sunny.repository.UserRepository;

// =====================
// 全画面共通：ログイン中ユーザー名をテンプレートに渡す仕組み
// =====================
//
// このクラスは @ControllerAdvice により、
// すべてのControllerが動く前に共通処理を行う役割を持つ。
//
// 【目的】
// ヘッダーなどで「ようこそ ○○さん」と表示するため、
// loginUserName という値を全画面で使えるようにしている。
//
// 【処理の流れ】
// 1. Spring Security が保持しているログイン情報（Principal）を取得
// 2. principal.getName() でログインID（メールアドレス）を取得
// 3. usersテーブルから該当ユーザーを検索
// 4. 見つかったユーザーの name を loginUserName として返す
// 5. Thymeleaf側では ${loginUserName} でどの画面でも使用可能になる
//
// これにより、各Controllerで毎回ユーザー名を渡す処理を書く必要がなくなる。
@ControllerAdvice
public class GlobalModelAdvice {

	private final UserRepository userRepository;

	public GlobalModelAdvice(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@ModelAttribute("loginUserName")
	public String loginUserName(Principal principal) {
		// 未ログイン時は null を返す
		if (principal == null)
			return null;

		// Spring Security が管理しているログインID（通常は email）
		String username = principal.getName();

		// emailでユーザー検索
		Optional<User> userOpt = userRepository.findByEmail(username);

		// 万が一emailで見つからなければ name でも検索
		if (userOpt.isEmpty()) {
			userOpt = userRepository.findByName(username);
		}

		// ユーザーが見つかれば name を返す
		return userOpt.map(User::getName).orElse(null);
	}
}
