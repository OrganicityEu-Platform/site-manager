package fr.cea.organicity.manager.domain;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    
    private String latitude;
    private String longitude;
    private String city;
    private String region;
    private String countryCode;
    private String country;

    private String wiki;
    
    private long quota;
    private long remQuota;
    private String created;
    private String updated;
    
    @JsonIgnore
    @ElementCollection(fetch=FetchType.EAGER)
    protected Set<String> managers = new HashSet<>();

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
        
    public boolean isCity() {
    	if (name.equals("provider") || name.equals("experimenters"))
    		return false;
    	else
    		return true;
    }
    
    @PrePersist
    protected void onCreate() {
    	urn = getUrn();
    	created = updated = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
    }

    @PreUpdate
    protected void onUpdate() {
    	urn = getUrn();
    	updated = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
    }

    @JsonIgnore
	public String getClientId() {
		return "ocsite-" + name;
	}
}
