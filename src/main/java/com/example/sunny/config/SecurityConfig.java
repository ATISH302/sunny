package com.example.sunny.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// =====================
// ログイン・権限・アクセス制御（Spring Security設定）
// =====================
//
// このクラスは、アプリ全体の「アクセスできるURL」と「ログイン処理」を決める設定。
// 例：
// ・ログインなしで見れるページ（/login, /register, /css/**, /images/** など）
// ・管理者だけが見れるページ（/admin/**）
// ・スタッフ/管理者だけが見れるページ（/staff/**）
// ・それ以外はログイン必須
//
// また、ログイン失敗した時に理由別（BAN / 停止）でURLを変える処理も入っている。
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	// =====================
	// セキュリティ全体のルール設定（URLごとの許可/拒否、ログイン/ログアウト）
	// =====================
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
				// =====================
				// URLごとのアクセス権限
				// =====================
				.authorizeHttpRequests(auth -> auth
						// ログイン不要で見れるページ（静的ファイル含む）
						.requestMatchers("/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()

						// 管理者だけOK
						.requestMatchers("/admin/**").hasRole("ADMIN")

						// スタッフ または 管理者 だけOK
						.requestMatchers("/staff/**").hasAnyRole("STAFF", "ADMIN")

						// それ以外はログイン必須
						.anyRequest().authenticated())

				// =====================
				// ログイン設定
				// =====================
				.formLogin(login -> login
						// ログイン画面URL
						.loginPage("/login")

						// ログイン処理を受けるURL（form action と一致させる）
						.loginProcessingUrl("/login")

						// ログイン成功時の遷移先
						.defaultSuccessUrl("/", true)

						// ログイン失敗時（理由別にURLを変える）
						.failureHandler(loginFailureHandler())

						.permitAll())

				// =====================
				// ログアウト設定
				// =====================
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login?logout=true"))

				// =====================
				// CSRF（フォーム送信の保護）
				// =====================
				.csrf(Customizer.withDefaults());

		return http.build();
	}

	// =====================
	// ログイン失敗時のハンドラ（BAN/停止など理由別で画面に戻す）
	// =====================
	//
	// DisabledException が投げられた時は「利用不可ユーザー」扱いになる。
	// このアプリでは message を使って
	// ・BANNED → /login?banned=true
	// ・SUSPENDED → /login?suspended=true
	// というようにログイン画面側で表示を切り替える想定。
	@Bean
	AuthenticationFailureHandler loginFailureHandler() {
		return new AuthenticationFailureHandler() {
			@Override
			public void onAuthenticationFailure(
					HttpServletRequest request,
					HttpServletResponse response,
					AuthenticationException exception) throws IOException, ServletException {

				if (exception instanceof DisabledException) {
					String msg = exception.getMessage();

					if ("BANNED".equalsIgnoreCase(msg)) {
						response.sendRedirect("/login?banned=true");
						return;
					}
					if ("SUSPENDED".equalsIgnoreCase(msg)) {
						response.sendRedirect("/login?suspended=true");
						return;
					}
				}

				// ★ InternalAuthenticationServiceException に包まれて来る場合（今回ココ）
				Throwable cause = exception.getCause();
				if (cause instanceof DisabledException) {
					String msg = cause.getMessage();

					if ("BANNED".equalsIgnoreCase(msg)) {
						response.sendRedirect("/login?banned=true");
						return;
					}
					if ("SUSPENDED".equalsIgnoreCase(msg)) {
						response.sendRedirect("/login?suspended=true");
						return;
					}
				}

				// それ以外は通常のログイン失敗
				response.sendRedirect("/login?error=true");
			}
		};
	}

	// =====================
	// パスワードの暗号化方式
	// =====================
	//
	// createDelegatingPasswordEncoder() を使うと、
	// {bcrypt} や {noop} など prefix 付きのパスワード形式にも対応できる。
	// data.sql に {noop} を使っている場合でも動く。
	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
