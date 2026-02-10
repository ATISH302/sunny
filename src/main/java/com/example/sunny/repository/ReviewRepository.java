package com.example.sunny.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.sunny.entity.Item;
import com.example.sunny.entity.Review;
import com.example.sunny.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	// 商品ごとのレビュー一覧（新しい順）
	List<Review> findByItemOrderByCreatedAtDesc(Item item);

	// 平均評価
	@Query("SELECT AVG(r.rating) FROM Review r WHERE r.item.id = :itemId")
	Double findAverageRatingByItemId(@Param("itemId") Long itemId);

	// レビュー件数
	long countByItemId(Long itemId);

	// ★ 同じユーザーが同じ商品にレビュー済みかどうかチェックするメソッド
	boolean existsByUserAndItem(User user, Item item);

	// （必要なら）直近のレビューを取りたい時用
	Review findFirstByUserAndItemOrderByCreatedAtDesc(User user, Item item);
}
