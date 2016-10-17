package fr.cea.organicity.manager.domain;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@Data
@Entity
public class OCUnit {
	
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
    	return "urn:oc:uom:" + name;
    }
	
	@JsonBackReference
	@ManyToMany(mappedBy="units", cascade={CascadeType.PERSIST}, fetch=FetchType.EAGER)
    private Collection<OCAttributeType> attributes = new ArrayList<>();
	
	@ManyToOne @NotNull
	@JsonManagedReference
	private OCDataType datatype;
	
    @PrePersist
    protected void onCreate() {
    	urn = getUrn();
    }

    @PreUpdate
    protected void onUpdate() {
    	urn = getUrn();
    }
}
