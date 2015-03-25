package com.sjtu.icarer.model;

import java.io.Serializable;

public  class User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2340304011060815823L;
	
	private int id;
	private String name;
	private String username;
	private String password="admin";//set for all user
	private int gender;
	private int age;
    private String photoUrl;
	private String secretKey;//use secret key to encrypt HTTP request param
	
	public User(int id) {
		this.setId(id);
	}
	
    public User(String username, String password) {
        this.setUsername(username); 
        this.setPassword(password);
	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getPhotoUrl() {
		return photoUrl;
	}


	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}


	public int getGender() {
		return gender;
	}


	public void setGender(int gender) {
		this.gender = gender;
	}


	public int getAge() {
		return age;
	}


	public void setAge(int age) {
		this.age = age;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getSecretKey() {
		return secretKey;
	}


	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

}
