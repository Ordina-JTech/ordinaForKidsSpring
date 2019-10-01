package org.ordina.ordinaForKids.config;

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

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		
		
		auth.inMemoryAuthentication()
        .withUser("admin").password(encoder().encode("adminPass")).roles("ADMIN", "USER")
        .and()
        .withUser("schooluser1").password(encoder().encode("school")).roles("USER")
		.and()
        .withUser("schooluser2").password(encoder().encode("school")).roles("USER")
		.and()
        .withUser("schooluser3").password(encoder().encode("school")).roles("USER");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.addFilterBefore(corsFilter(), SessionManagementFilter.class)
		.httpBasic().and()
	    .csrf().disable()
	    .formLogin().disable()
	    .authorizeRequests()
        .antMatchers(HttpMethod.GET, "/**").hasRole("USER")
        .antMatchers(HttpMethod.POST, "/user/**").hasRole("ADMIN")
        ;
	    
	}
	
	@Bean
	public PasswordEncoder  encoder() {
	    return new BCryptPasswordEncoder();
	}
	
	@Bean
    CorsFilter corsFilter() {
        CorsFilter filter = new CorsFilter();
        return filter;
    }
}
