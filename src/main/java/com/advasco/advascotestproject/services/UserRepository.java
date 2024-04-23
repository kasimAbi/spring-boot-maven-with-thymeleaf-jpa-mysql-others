package com.advasco.advascotestproject.services;

import org.springframework.data.jpa.repository.JpaRepository;

import com.advasco.advascotestproject.model.User;

public interface UserRepository extends JpaRepository<User, Integer>{
    
}
