package fr.cea.organicity.manager.domain;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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
	
    private String created;
    private String updated;
    
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
}
