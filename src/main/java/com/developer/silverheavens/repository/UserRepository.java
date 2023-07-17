package com.developer.silverheavens.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.developer.silverheavens.entities.User;


public interface UserRepository extends JpaRepository<User, Integer>,JpaSpecificationExecutor<User>{

}
