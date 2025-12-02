package com.example.sunny.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.sunny.entity.User;
import com.example.sunny.repository.UserRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	// 🔐 パスワードエンコーダー（必須）
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	// 🔹 ログイン時に「ユーザー名で探して認証する」設定
	@Bean
	UserDetailsService userDetailsService(UserRepository userRepository) {

		return username -> {
			// ← ここが「名前でログイン」のポイント
			User user = userRepository.findByName(username);

			if (user == null) {
				throw new UsernameNotFoundException("ユーザーが見つかりません: " + username);
			}

			// ROLE_ を付けた形に変換 (例: CUSTOMER → ROLE_CUSTOMER)
			String roleName = "ROLE_" + user.getRole();

			return new org.springframework.security.core.userdetails.User(
					user.getName(), // ← ログインIDとして「名前」を使う
					user.getPassword(),
					user.isEnabled(),
					true,
					true,
					true,
					List.of(new SimpleGrantedAuthority(roleName)));
		};
	}

	// 🔹 URL のアクセス制御 & ログイン画面の設定
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								"/login",
								"/register",
								"/css/**",
								"/js/**",
								"/images/**")
						.permitAll() // → ログイン前に誰でもアクセスできる
						.anyRequest().authenticated() // → それ以外はログイン必須
				)
				.formLogin(form -> form
						.loginPage("/login") // カスタムログイン画面
						.loginProcessingUrl("/login") // POST /login
						.defaultSuccessUrl("/", true) // ログイン後にTOPへ
						.failureUrl("/login?error=true") // 失敗
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login?logout=true")
						.permitAll());

		return http.build();
	}

	// AuthenticationManager を利用可能にする（必要な場合）
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}