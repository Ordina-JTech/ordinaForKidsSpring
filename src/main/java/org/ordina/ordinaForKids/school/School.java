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
