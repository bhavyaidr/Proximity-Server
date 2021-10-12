package com.proximity.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
public class LoginAndSignup {
	
	@NotBlank(message = "username name can`t be empty...")
	@Size(min = 5, message = "username must be greater than 4 characters!")
	@Id
	private String username;
	
	@NotBlank(message = "password can`t be empty...")
	@Size(min = 5, message = "password must be greater than 4 characters!")
	private String password;
	
	private String fav_dish;
	
	public LoginAndSignup() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LoginAndSignup(String username,String password,String fav_dish) {
		super();
		this.username = username;
		this.password = password;
		this.fav_dish = fav_dish;
	}
	
	public String getFav_dish() {
		return fav_dish;
	}
	public void setFav_dish(String fav_dish) {
		this.fav_dish = fav_dish;
	}
	public String getusername() {
		return username;
	}
	public void setusername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "LoginAndSignup [username=" + username + ", password=" + password + ", fav_dish=" + fav_dish + "]";
	}
	
	

}
