package com.developer.silverheavens;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


// Adding bookings & testing 
// FOR FINAL REVIEW 26-06-2023
// FOR REVIEW 29-06-2023
@SpringBootApplication
//@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class SilverheavensApplication {
	
	private static final Logger logger =  LogManager.getLogger("MyLoggerOne");

	public static void main(String[] args) {
		logger.info("Started App");
		SpringApplication.run(SilverheavensApplication.class, args);
	}
	
//	@Bean
//	public PasswordEncoder createPasswordEncoder()
//	{
//		return new BCryptPasswordEncoder();
//	}

}
