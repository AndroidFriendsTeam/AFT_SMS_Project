package com.exemple.aft_SMS_Project;

public class phoneNumber {
	
	String idPhoneNumber, phoneNumber;

	public String getIdPhoneNumber() {
		return idPhoneNumber;
	}

	public void setIdPhoneNumber(String idPhoneNumber) {
		this.idPhoneNumber = idPhoneNumber;
	}

	public phoneNumber(String idPhoneNumber, String phoneNumber) {
		super();
		this.idPhoneNumber = idPhoneNumber;
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}
