package org.ordina.ordinaForKids.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Pattern.Flag;

import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "user")
public class User {
	
	@Id
    @Column(name = "email", nullable = false)
	@Pattern(message = "Email pattern cannot be validated" , regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", flags = Flag.UNICODE_CASE)
	private String email;
	
	
	@Column(name = "password", nullable = false)
	@Length(min = 5, message = "Your password must have at least 5 characters")
	private String password;
	
	@Column(name = "firstname", nullable = false)
	@Length(min = 2, message = "First name must be atleast 2 characters")
	private String firstname;
	
	@Column(name = "lastname", nullable = false)
	@Length(min = 2, message = "Last name must be atleast 2 characters")
	private String lastname;
	
	@Column(name = "userrole", nullable = false)
	@Pattern(message = "Role must be Administrator, School or Ordina Employee", regexp = "^.*(Administrator|School|OrdinaEmployee).*$")
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
	public UserRole getUserrole() {
		return UserRole.valueOf(
				this.userrole.startsWith("ROLE_") ? this.userrole.substring(5) : this.userrole
				);
	}
	/**
	 * @param userRole the userRole to set
	 */
	public void setUserrole(UserRole userrole) {
		this.userrole = "ROLE_" + userrole.toString();
	}
	
	
}
