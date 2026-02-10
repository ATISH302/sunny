package com.example.sunny.service;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.sunny.entity.User;
import com.example.sunny.entity.UserStatus;
import com.example.sunny.repository.UserRepository;

// =====================
// ログイン認証：ユーザー取得＋停止ユーザー判定
// =====================
//
// このクラスは Spring Security の UserDetailsService を実装し、
// 「ログイン時にユーザー情報を取得する処理」を担当する。
//
// 主な役割：
// ・ログインID（メール or 名前）からユーザーを検索
// ・BAN / SUSPENDED ユーザーをログイン不可にする
// ・問題なければ CustomUserDetails を返す
//
// ※ このクラスは SecurityConfig から自動的に呼び出される
//

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	// =====================
	// ログイン時に呼ばれるメソッド
	// =====================
	//
	// input：
	// ・ログイン画面で入力された「ユーザー名」
	// ・メールアドレス or 名前 のどちらも許可している
	//
	@Override
	public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {

		// =====================
		// ユーザー検索（email → name の順で探す）
		// =====================
		User user = userRepository.findByEmail(input)
				.or(() -> userRepository.findByName(input))
				.orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + input));

		// =====================
		// ★ ステータス判定（ここが超重要）
		// =====================
		//
		// BAN または SUSPENDED の場合は、
		// ログイン処理を強制的に失敗させる。
		//
		// DisabledException を投げることで、
		// SecurityConfig の failureHandler が理由を判別し、
		// ・/login?banned=true
		// ・/login?suspended=true
		// にリダイレクトできる。
		//

		if (user.getStatus() == UserStatus.BANNED) {
			// 永久停止ユーザー
			throw new DisabledException("BANNED");
		}

		if (user.getStatus() == UserStatus.SUSPENDED) {
			// 一時停止ユーザー
			throw new DisabledException("SUSPENDED");
		}

		// =====================
		// 問題なければログイン許可
		// =====================
		//
		// CustomUserDetails は
		// ・権限（ROLE_ADMIN / ROLE_STAFF など）
		// ・パスワード
		// ・ユーザー情報
		// を Spring Security に渡すためのクラス
		//
		return new CustomUserDetails(user);
	}
}
