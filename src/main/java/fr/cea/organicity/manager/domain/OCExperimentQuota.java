package fr.cea.organicity.manager.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.Min;

import org.springframework.dao.DataIntegrityViolationException;

import lombok.Data;

@Data
@Entity
public class OCExperimentQuota {
	
	@Id
	private String id;
		
	@Min(0)
	private long quota;
	
	@Min(0)
	private long nbAsets;
      
    @PrePersist
    protected void onCreate() {
    	if (nbAsets > quota)
    		throw new DataIntegrityViolationException("Over quota entity. Rejected.");
    }

    @PreUpdate
    protected void onUpdate() {
    	if (nbAsets > quota)
    		throw new DataIntegrityViolationException("Over quota entity. Rejected.");
    }
}
