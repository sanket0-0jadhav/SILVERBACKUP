package com.developer.silverheavens.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.developer.silverheavens.dto.ResponseDto;
import com.developer.silverheavens.dto.ResponseStatus;
import com.developer.silverheavens.entities.JwtRequest;
import com.developer.silverheavens.entities.User;
import com.developer.silverheavens.security.SilverHeavensUserDetailsService;
import com.developer.silverheavens.service.UserService;
import com.developer.silverheavens.util.JwtUtil;

@RestController
@RequestMapping("/user")
public class JwtController {
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private SilverHeavensUserDetailsService userDetailService;
	
	private static final Logger logger =  LogManager.getLogger("MyLoggerOne");

	@PostMapping("login")
	public ResponseEntity<ResponseDto<String>> login(@RequestBody JwtRequest jwtRequest) throws Exception {
		String username = jwtRequest.getUsername();
		String password = jwtRequest.getPassword();
		
		logger.info("Login request for : "+username);
		
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (Exception e) {
			logger.info("Login Exception for : "+username+" :: "+e);
			throw new Exception("Bad Credentials");
		}
		
		UserDetails userDetails = userDetailService.loadUserByUsername(username);
		String token = jwtUtil.generateToken(userDetails);
	
		ResponseDto<String> resp = new ResponseDto<String>(ResponseStatus.SUCCESS,token,null);
		return new ResponseEntity<ResponseDto<String>>(resp,HttpStatus.OK);
	}

	
	@PostMapping("register")
	public ResponseEntity<ResponseDto<String>> register(@RequestBody User user) throws Exception {
		
		logger.info("Register request for : "+user.getUsername());
		
		if(userService.registerUser(user)) {			
			logger.info("Register SUCCESS : "+user.getUsername());
			ResponseDto<String> resp = new ResponseDto<String>(ResponseStatus.SUCCESS,"User Registered.",null);
			return new ResponseEntity<ResponseDto<String>>(resp,HttpStatus.OK);
		}else {
			logger.info("Register FAILED : "+user.getUsername());
			ResponseDto<String> resp = new ResponseDto<String>(ResponseStatus.FAIL,"Cannot create user.",null);
			return new ResponseEntity<ResponseDto<String>>(resp,HttpStatus.OK);
		}
	}
	
	@GetMapping("/oauth")
	public String home() {
		return "home";
	}
    
}