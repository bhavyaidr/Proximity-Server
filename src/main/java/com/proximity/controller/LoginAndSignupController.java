package com.proximity.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proximity.dao.LoginAndSignupDao;
import com.proximity.entities.LoginAndSignup;

@Controller
public class LoginAndSignupController {
	
	@Autowired
	private LoginAndSignupDao signupService;
	
	@GetMapping("/")
	public String home() {
		return "index";
	}

	@PostMapping("/signup")
	@ResponseBody
	public String signUp(@Valid @ModelAttribute("signup") LoginAndSignup signup, BindingResult result) {
		boolean saved=signupService.saveRecord(signup);
		if(saved==false)result.addError(new ObjectError("username", "username already exists"));
		System.out.println(saved);
		return "sign up completed";
	}

	
	
}
