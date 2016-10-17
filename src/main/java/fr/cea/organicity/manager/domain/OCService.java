package fr.cea.organicity.manager.domain;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@ToString(exclude="site")
public class OCService {

	@Id
	private String urn;
	
	@ValidateName
    private String name;
	
	private String description;
	private String related;
	
	@ManyToOne @NotNull
	@JsonBackReference
	private OCSite site;
	
    private long created;
    private long updated;
    
    public String getUrn() {
    	if (site != null)
    		return computeUrn(site.getName(), name);
    	else
    		return computeUrn(null, name);
    }
    
    public static String computeUrn(String siteName, String serviceName) {
    	if (siteName != null)
    		return OCSite.computeUrn(siteName) + ":" + serviceName;
    	else
    		return "<<not available>>";
    }
    
    public Date getCreatedDate() {
    	return new Date(created);
    }
    
    public Date getUpdatedDate() {
    	return new Date(updated);
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
