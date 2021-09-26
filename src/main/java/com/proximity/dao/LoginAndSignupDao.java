package com.proximity.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proximity.entities.LoginAndSignup;
import com.proximity.repository.LoginAndSignupRepo;

@Service
public class LoginAndSignupDao {
	
	@Autowired
	private LoginAndSignupRepo loginrepo;

	public boolean saveRecord(LoginAndSignup signup) {
		if(loginrepo.existsById(signup.getusername())!=true) {
			loginrepo.save(signup);
			return true;
		}
		else return false; 
	}
	
	//getAllUsername  

}
