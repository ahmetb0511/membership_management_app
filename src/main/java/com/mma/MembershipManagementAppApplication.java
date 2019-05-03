package com.mma;

import java.text.Collator;
import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.mma.common.datatable.repository.JpaDataTableRepositoryFactoryBean;

@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = JpaDataTableRepositoryFactoryBean.class)
public class MembershipManagementAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(MembershipManagementAppApplication.class, args);
	}

	@Bean
	public Collator collator() {
		return Collator.getInstance(Locale.ENGLISH);
	}
}
