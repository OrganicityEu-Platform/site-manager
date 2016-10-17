package fr.cea.organicity.manager.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
public class OCError {

	@Id @GeneratedValue
	private long id;

	@NotNull private Date date; 
	@NotNull private String status;
	@NotNull private String error;
	@NotNull @Column(columnDefinition = "TEXT") private String message;
	@NotNull private String path;
	@NotNull @Column(columnDefinition = "TEXT") private String model;
	
	// needed by Hibernate
	public OCError() {
	}
	
	public OCError(Date date, String status, String error, String message, String path, String model) {
		this.date = date;
		this.status = status;
		this.error = error;
		this.message = message;
		this.path = path;
		this.model = model;
	}
}
