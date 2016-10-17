package fr.cea.organicity.manager.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import lombok.Data;

@Data
@Entity
public class OCUnregisteredAssetType {
	@Id
	private String urn;
	
	@ValidateName
    private String name;
	
    public String getUrn() {
    	return computeUrn(name);
    }
    
    public static String computeUrn(String name) {
    	return "urn:oc:entitytype:" + name;
    }
	
    @PrePersist
    protected void onCreate() {
    	urn = getUrn();
    }

    @PreUpdate
    protected void onUpdate() {
    	urn = getUrn();
    }
}
