package com.proximity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proximity.entities.LoginAndSignup;

public interface LoginAndSignupRepo extends JpaRepository<LoginAndSignup, String> {

}
