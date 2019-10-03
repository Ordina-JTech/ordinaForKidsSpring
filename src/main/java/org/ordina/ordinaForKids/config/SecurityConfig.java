package org.ordina.ordinaForKids.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.SessionManagementFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	  DataSource dataSource;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.jdbcAuthentication()
			.dataSource(dataSource)
			.passwordEncoder(encoder())
			.usersByUsernameQuery("SELECT email as username, password, TRUE FROM user WHERE email = ?")
			.authoritiesByUsernameQuery("SELECT email as username, userrole as role FROM user WHERE email = ?" )
			.and().inMemoryAuthentication().withUser("admin").password(encoder().encode("admin")).roles("Administrator");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.addFilterBefore(corsFilter(), SessionManagementFilter.class)
		.httpBasic().and()
	    .csrf().disable()
	    .formLogin().disable()
	    .authorizeRequests()
        .antMatchers(HttpMethod.GET, "/calendar_event/**").permitAll()
        .antMatchers(HttpMethod.GET, "/login/**").permitAll()
        .antMatchers(HttpMethod.POST, "/user/**").hasRole("Administrator")
        .antMatchers(HttpMethod.PUT, "/user/**").hasRole("Administrator")
        .antMatchers(HttpMethod.GET, "/user/**").hasRole("Administrator")
        ;
	    
	}
	
	@Bean
	public BCryptPasswordEncoder  encoder() {
	    return new BCryptPasswordEncoder();
	}
	
	@Bean
    CorsFilter corsFilter() {
        CorsFilter filter = new CorsFilter();
        return filter;
    }
}
