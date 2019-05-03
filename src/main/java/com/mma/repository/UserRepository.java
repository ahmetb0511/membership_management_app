package com.mma.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mma.common.datatable.repository.JpaDataTableRepository;
import com.mma.common.enums.UserEnums.Status;
import com.mma.domain.Unit;
import com.mma.domain.User;
import com.mma.domain.UserRole;


public interface UserRepository extends JpaDataTableRepository<User, Integer> {
	
	public Page<User> findByStatusNot(Status status, Pageable pageable);
	
	public User findByIdAndStatusNot(int id, Status status);
	
	public User findById(int id);

	public User findByEmailAndStatusNot(String email, Status status);
	
	public User findByEmail(String email);
	
	public User findByEmailToken(String emailToken);
	
	public List<User> findByStatus(Status status);
	
	public List<User> findByUnitAndStatus(Unit unit, Status status);
	
	public List<User> findByRoleAndStatus(UserRole userRole, Status status);
	
}
