package com.developer.silverheavens.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	@Autowired
	SilverHeavensUserDetailsService userDetailService;
    @Autowired
    private JwtAuthenticationFilter jwtFilter;
	
	//AM
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	//FILTER CHAIN
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
		.csrf((csrf) -> csrf.disable())
		.authorizeHttpRequests((authReq)->{
			authReq
			//.anyRequest().permitAll()
			.requestMatchers(HttpMethod.GET,"/user/oauth").permitAll()
			.requestMatchers(HttpMethod.POST,"/user/**").permitAll()
			.requestMatchers(HttpMethod.GET,"/rates/**").hasAnyRole("ADMIN","USER")
			.requestMatchers(HttpMethod.POST,"/rates/**").hasRole("ADMIN")
			.requestMatchers(HttpMethod.PATCH,"/rates/**").hasRole("ADMIN")
			.requestMatchers(HttpMethod.DELETE,"/rates/**").hasRole("ADMIN")
			.requestMatchers(HttpMethod.GET,"/booking/**").hasAnyRole("ADMIN","USER")
			.requestMatchers(HttpMethod.POST,"/booking/**").hasRole("ADMIN")
			.requestMatchers(HttpMethod.PATCH,"/booking/**").hasRole("ADMIN")
			.requestMatchers(HttpMethod.DELETE,"/booking/**").hasRole("ADMIN")
//			.anyRequest().authenticated()
			;
		})		
		.oauth2Client((oauth)->{
			oauth.init(http);
		})
//		.formLogin((req)->{
//			req.permitAll();
//		})
//		.logout((logout)->{
//			logout.permitAll();
//		})
//		.httpBasic((basic)->{
//			
//		})
		.sessionManagement((sm)->{
			sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		})
		.userDetailsService(userDetailService)
		.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
		;
		 
		return http.build();
	}
	
	
	//PASSWORD ENCODER
	@Bean
	public PasswordEncoder passEncoder() {
		return new BCryptPasswordEncoder();
//		return NoOpPasswordEncoder.getInstance();
	}
	
}
