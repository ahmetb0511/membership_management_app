package com.mma.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mma.domain.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
	
	UserRole findByName(String name);
	
}
