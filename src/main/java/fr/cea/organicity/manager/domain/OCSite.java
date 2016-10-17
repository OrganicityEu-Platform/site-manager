package fr.cea.organicity.manager.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@Data
@Entity
public class OCSite {
	
	@Id
	private String urn;
	
	@ValidateName
    private String name;
	
    @Email @NotNull
    private String email;
    private String related;
    private long quota;
    private long remQuota;
    private long created;
    private long updated;
    
    @JsonManagedReference
    @OneToMany(mappedBy="site", cascade={CascadeType.PERSIST, CascadeType.REMOVE}, fetch=FetchType.EAGER)
    private Collection<OCService> services = new ArrayList<>();
    
    public String getUrn() {
    	return computeUrn(name);
    }
    
    public static String computeUrn(String name) {
    	return "urn:oc:site:" + name;
    }
    
    public OCService getService(String serviceName) {
    	for (OCService service : services)
    		if (service.getName().equals(serviceName))
    			return service;
    	return null;
    }
    
    public Date getCreatedDate() {
    	return new Date(created);
    }
    
    public Date getUpdatedDate() {
    	return new Date(updated);
    }
    
    public boolean isCity() {
    	if (name.equals("provider") || name.equals("experimenters"))
    		return false;
    	else
    		return true;
    }
    
    @PrePersist
    protected void onCreate() {
    	urn = getUrn();
    	created = updated = Calendar.getInstance().getTimeInMillis();
    }

    @PreUpdate
    protected void onUpdate() {
    	urn = getUrn();
    	updated = Calendar.getInstance().getTimeInMillis();
    }
}
