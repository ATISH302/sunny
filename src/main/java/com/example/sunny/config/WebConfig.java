package com.example.sunny.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// =====================
// アップロード画像の表示を有効にする設定
// =====================
//
// 目的：
// /uploads/** というURLで、PC内（サーバー内）の uploads フォルダに保存した画像を表示できるようにする。
//
// 例：
// 保存先： uploads/abc.png
// 表示URL： http://localhost:8080/uploads/abc.png
//
// これが無いと、MultipartFileで保存しても「ブラウザから画像が見れない」状態になる。
@Configuration
public class WebConfig implements WebMvcConfigurer {

	// uploads の保存先フォルダ名（application.properties で app.upload.dir を変えられる）
	@Value("${app.upload.dir:uploads}")
	private String uploadDir;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		// uploads フォルダの絶対パスを作る
		Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

		// file:///... という形式のURLに変換（Springが静的リソースとして読める形）
		String location = uploadPath.toUri().toString();

		// /uploads/** でアクセスが来たら、上の location（uploadsフォルダ）から探して返す
		registry.addResourceHandler("/uploads/**")
				.addResourceLocations(location);
	}
}
