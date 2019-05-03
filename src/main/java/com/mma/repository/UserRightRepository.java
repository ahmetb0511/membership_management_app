package com.mma.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.mma.domain.User;
import com.mma.domain.UserRight;

public interface UserRightRepository extends JpaRepository<UserRight, Integer> {
	
	List<UserRight> findByUser(User user);
	
	@Transactional
	void deleteByUser(User user);
	
}
