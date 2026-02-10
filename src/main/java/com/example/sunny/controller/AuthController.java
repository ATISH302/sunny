//LoginController.java

package com.example.sunny.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// =====================
// 認証関連：ログイン・ログアウト画面制御
// =====================
//
// この Controller は、Spring Security と連携して
// ログイン画面の表示やエラーメッセージの制御を行う。
//
// 主な役割：
// ・ログイン画面の表示
// ・ログイン失敗時のエラーメッセージ表示
// ・BAN / SUSPENDED ユーザーへのメッセージ表示
//
// 実際の認証処理（ID・パスワードのチェック）は
// Spring Security（SecurityConfig）が担当し、
// この Controller は「画面制御のみ」を行う。
//

@Controller
public class AuthController {

	// =====================
	// ログイン画面表示
	// =====================
	//
	// URL：
	// ・GET /login
	//
	// 機能概要：
	// ・ログイン画面（login.html）を表示する
	// ・ログイン失敗やBAN状態などに応じて
	//   メッセージを画面に表示する
	//

	// Spring Security の formLogin 設定と連動しており、
	// 実際のログイン処理は SecurityConfig 側で行われる。
	//
	@GetMapping("/login")
	public String login(
			Model model,
			String error,
			String logout,
			String banned,
			String suspended) {

		// =====================
		// ログイン失敗時
		// =====================
		//
		// ID またはパスワードが間違っている場合、
		// /login?error=true でこの画面に戻される
		//
		if (error != null) {
			model.addAttribute(
					"errorMessage",
					"メールアドレスまたはパスワードが正しくありません");
		}

		// =====================
		// ログアウト完了時
		// =====================
		//
		// /logout 後に
		// /login?logout=true にリダイレクトされる
		//
		if (logout != null) {
			model.addAttribute(
					"message",
					"ログアウトしました");
		}

		// =====================
		// BAN ユーザーの場合
		// =====================
		//
		// SecurityConfig の AuthenticationFailureHandler により
		// BANNED 状態のユーザーは
		// /login?banned=true にリダイレクトされる
		//
		if (banned != null) {
			model.addAttribute(
					"errorMessage",
					"このアカウントは利用停止（BAN）されています");
		}

		// =====================
		// 一時停止（SUSPENDED）ユーザーの場合
		// =====================
		//
		// 一時的に利用停止されているユーザー向けのメッセージ
		//
		if (suspended != null) {
			model.addAttribute(
					"errorMessage",
					"このアカウントは一時停止中です");
		}

		return "login";
	}

}
