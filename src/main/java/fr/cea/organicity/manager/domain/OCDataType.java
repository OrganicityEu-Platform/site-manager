package fr.cea.organicity.manager.domain;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;

@Data
@Entity
public class OCDataType {

	@Id
	private String urn;
	
	@ValidateName
    private String name;
		
    public String getUrn() {
    	return computeUrn(name);
    }
    
    public static String computeUrn(String name) {
    	return "urn:oc:dataType:" + name;
    }
    
    @JsonBackReference
    @OneToMany(mappedBy="datatype", cascade={CascadeType.REFRESH}, fetch=FetchType.EAGER)
    private Collection<OCUnit> units = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
    	urn = getUrn();
    }

    @PreUpdate
    protected void onUpdate() {
    	urn = getUrn();
    }
}
