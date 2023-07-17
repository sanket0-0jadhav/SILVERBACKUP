package com.developer.silverheavens.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.developer.silverheavens.entities.User;
import com.developer.silverheavens.repository.UserRepository;
import com.developer.silverheavens.specifications.UserSepcification;

@Service
public class SilverHeavensUserDetailsService implements UserDetailsService{

	@Autowired
	UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Optional<User> userOptional = userRepository.findOne(UserSepcification.byUsername(username));
		if(userOptional.isEmpty())
			throw new UsernameNotFoundException(username);
		
		
		return new SilverHeavensUserDetails(userOptional.get());
	}

}
