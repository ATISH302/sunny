package com.example.sunny.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.sunny.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	// ログイン用：名前でユーザーを探す
	User findByName(String name);

	User findByEmail(String email);
}
