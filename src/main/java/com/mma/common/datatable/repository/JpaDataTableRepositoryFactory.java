package com.mma.common.datatable.repository;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.EntityManager;
import java.io.Serializable;


public class JpaDataTableRepositoryFactory<T, ID extends Serializable> extends JpaRepositoryFactory {

	public JpaDataTableRepositoryFactory(EntityManager entityManager) {
		super(entityManager);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected SimpleJpaRepository<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
		JpaEntityInformation<T, Serializable> entityInformation = (JpaEntityInformation<T, Serializable>) getEntityInformation(information.getDomainType());
		Class<?> repositoryInterface = information.getRepositoryInterface();
		return JpaDataTableRepository.class.isAssignableFrom(repositoryInterface)
				? new JpaDataTableRepositoryImpl<T, ID>(entityInformation, entityManager)
				: super.getTargetRepository(information, entityManager);
	}

	protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
		return JpaDataTableRepositoryImpl.class;
	}
	
}
