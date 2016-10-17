package fr.cea.organicity.manager.domain;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@Data
@Entity
public class OCAssetType {
	
	@Id
	private String urn;
	
	@ValidateName
    private String name;
	
	private String description;
	private String related;
	
    public String getUrn() {
    	return computeUrn(name);
    }
    
    public static String computeUrn(String name) {
    	return "urn:oc:entitytype:" + name;
    }
        
	@ManyToMany
	@JsonManagedReference
    private Collection<OCAttributeType> attributes = new ArrayList<>();
	
    @PrePersist
    protected void onCreate() {
    	urn = getUrn();
    }

    @PreUpdate
    protected void onUpdate() {
    	urn = getUrn();
    }
}
