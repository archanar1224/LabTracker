package org.nimhans.EHealthCare.model;

public abstract class User {

	private String userId;
	private String name;
	//in derived classes we can have lab to which he belongs
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	//String designation;
	//String password;
	public User(String userId, String name) {
		super();
		this.userId = userId;
		this.name = name;
	}

}
