package org.ordina.ordinaForKids.user;

import javax.persistence.Entity;
import javax.persistence.Table;


public class UserDTO {
	private String email;
	private String password;
	private String firstname;
	private String lastname;
	private String userrole;
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}
	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}
	/**
	 * @param lastname the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	/**
	 * @return the userRole
	 */
	public String getUserrole() {
		return userrole;
	}
	/**
	 * @param userRole the userRole to set
	 */
	public void setUserrole(String userrole) {
		this.userrole = userrole;
	}
	
	
}
