package com.example.sunny.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sunny.entity.Report;
import com.example.sunny.entity.Review;
import com.example.sunny.entity.User;

public interface ReviewReportRepository extends JpaRepository<Report, Long> {

	// 同じユーザーが同じレビューを何度も通報できないようにチェック
	boolean existsByReviewAndReportedBy(Review review, User reportedBy);
}
