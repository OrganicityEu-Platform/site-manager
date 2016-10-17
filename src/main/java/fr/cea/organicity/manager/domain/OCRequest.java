package fr.cea.organicity.manager.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
@Entity
public class OCRequest {

	@Id @GeneratedValue
	private long id;
	
	@NotNull
	private Date date;
	
	@NotNull
	private String status;
	
	@NotNull @Size(min=2)
	private String method;
	
	private String sub;

	private long duration;
	
	@Column(columnDefinition = "TEXT")
	private String message;
}
