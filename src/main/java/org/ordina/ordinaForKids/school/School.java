package org.ordina.ordinaForKids.school;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "school")
@EntityListeners(AuditingEntityListener.class)
public class School {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
	
	@Column(name = "contact_firstname", nullable = false)
    private String firstName;
    @Column(name = "contact_lastname", nullable = false)
    private String lastName;
    @Column(name = "contact_email", nullable = false)
    private String email;
    @Column(name = "contact_password", nullable = false)
    private String password;
    @Column(name = "ordina_owner_id", nullable = false)
    private Long ordinaOwner;
    
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
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
	 * @return the ordinaOwner
	 */
	public Long getOrdinaOwner() {
		return ordinaOwner;
	}
	/**
	 * @param ordinaOwner the ordinaOwner to set
	 */
	public void setOrdinaOwner(Long ordinaOwner) {
		this.ordinaOwner = ordinaOwner;
	}
    
    
}
