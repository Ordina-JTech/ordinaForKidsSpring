package org.ordina.ordinaForKids.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "user")
public class User {
	
	@Id
    @Column(name = "email", nullable = false)
	private String email;
	
	
	@Column(name = "password", nullable = false)
	@Length(min = 5, message = "Your password must have at least 5 characters")
	private String password;
	
	@Column(name = "firstname", nullable = false)
	private String firstname;
	
	@Column(name = "lastname", nullable = false)
	private String lastname;
	
	@Column(name = "userrole", nullable = false)
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
