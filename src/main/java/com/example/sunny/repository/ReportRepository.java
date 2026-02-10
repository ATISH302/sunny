package com.example.sunny.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sunny.entity.Report;
import com.example.sunny.entity.Review;
import com.example.sunny.entity.User;

public interface ReportRepository extends JpaRepository<Report, Long> {

	// 同じユーザーが同じレビューを二重通報していないか判定
	boolean existsByReviewAndReportedBy(Review review, User reportedBy);

	// 新しい順で一覧取得
	List<Report> findAllByOrderByCreatedAtDesc();

	// ★同じレビューに対する通報件数
	long countByReviewId(Long reviewId);

}
