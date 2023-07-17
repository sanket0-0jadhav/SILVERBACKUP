package com.developer.silverheavens.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.developer.silverheavens.entities.User;
import com.developer.silverheavens.exceptions.ValidationException;
import com.developer.silverheavens.repository.UserRepository;
import com.developer.silverheavens.specifications.UserSepcification;

import jakarta.persistence.EntityManager;
import validators.CustomValidator;
import validators.ValidatorResult;

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PasswordEncoder passEncoder;
	
	@Autowired
	EntityManager entityManager;
	
	public boolean registerUser(User user) {
		/*Validation*/
		ValidatorResult result = CustomValidator.validateNewUser(user);
		if(!result.isValidationPassed()) {
			throw new ValidationException(result.getMessage().toString());
		}
		
		//check if already exists
		Optional<User> userOptional = userRepository.findOne(UserSepcification.byUsername(user.getUsername()));
		if(userOptional.isPresent()) {
			throw new ValidationException("User already present with username : "+user.getUsername());
		}
		user.setPassword(passEncoder.encode(user.getPassword()));
		userRepository.save(user);
		entityManager.clear();
		return true;
	}
	
}
