package com.mma.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.mma.userdetails.UserPrincipalService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/login/impersonate*").hasRole("ADMINISTRATOR")
				.antMatchers("/logout/impersonate*").hasRole("PREVIOUS_ADMINISTRATOR")
				.antMatchers("/units*").hasRole("ADMINISTRATOR")
				.antMatchers("/users*").hasRole("VIEW_USERS")
				.antMatchers("/reports*").hasRole("VIEW_REPORTS")
				.antMatchers("/feeTypes*").hasRole("VIEW_FEE_TYPES")
				.antMatchers("/fees*").hasRole("VIEW_FEES")
				.antMatchers("/forgot-password").permitAll()			
				.antMatchers("/reset-password/*").permitAll()
				.anyRequest().fullyAuthenticated()
				.and()
			.formLogin()
				.loginPage("/login")
				.permitAll()
				.and()
			.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.permitAll()
				.and()
			.csrf()
				.disable()
			.addFilterAfter(switchUserFilter(), FilterSecurityInterceptor.class);
	}
    
	@Bean
	public SwitchUserFilter switchUserFilter() {
		SwitchUserFilter filter = new SwitchUserFilter();
		filter.setUserDetailsService(userDetailsService());
		filter.setTargetUrl("/");
		
		return filter;
	}
	
    @Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/assets/**");
	}

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, UserPrincipalService userDetailsService) throws Exception {
    	auth
    	.userDetailsService(userDetailsService)
    	.passwordEncoder(passwordEncoder());
    }
    
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
