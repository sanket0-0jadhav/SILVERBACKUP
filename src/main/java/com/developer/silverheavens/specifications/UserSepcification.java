package com.developer.silverheavens.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.developer.silverheavens.entities.User;


public interface UserSepcification {

	//get by id
	public static Specification<User> byUsername(String username){
		return (root,query,cb)->{return cb.equal(root.get("username"), username);};
	}
	
}
