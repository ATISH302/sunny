package com.example.sunny.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService {

	@Value("${app.upload.dir:uploads}")
	private String uploadDir;

	public String save(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			return null;
		}

		try {
			// uploads フォルダを作る
			Path dirPath = Paths.get(uploadDir).toAbsolutePath().normalize();
			Files.createDirectories(dirPath);

			// 拡張子を維持（.jpg/.png など）
			String original = file.getOriginalFilename();
			String ext = "";
			if (original != null && original.contains(".")) {
				ext = original.substring(original.lastIndexOf("."));
			}

			// 保存ファイル名（ユニーク）
			String filename = UUID.randomUUID().toString() + ext;

			Path savePath = dirPath.resolve(filename);
			file.transferTo(savePath.toFile());

			// DBに入れるのは Web から見えるパス
			return "/uploads/" + filename;

		} catch (IOException e) {
			throw new RuntimeException("画像の保存に失敗しました: " + e.getMessage(), e);
		}
	}
}
