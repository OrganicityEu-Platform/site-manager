package fr.cea.organicity.manager.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
@Entity
public class OCApiCall {

	@Id @GeneratedValue
	private long id;
	
	@NotNull
	private Date date;
	
	@NotNull @Size(min=2)
	private String url;
	
	private long duration;
}
