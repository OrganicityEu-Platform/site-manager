package fr.cea.organicity.manager.controllers.ui;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.cea.organicity.manager.domain.OCDataType;
import fr.cea.organicity.manager.domain.OCUnit;
import fr.cea.organicity.manager.repositories.OCDataTypeRepository;

@Controller
@RequestMapping("/dictionaries/datatypes")
public class DicoDataTypeControllerUI implements Dico {
	
	@Autowired private DictionaryHelper dicoHelper;
	@Autowired private OCDataTypeRepository datatypeRepository;
	
	
	@GetMapping
	@PreAuthorize("hasRole('APP:dictionary-user')")
	public String datatypesGET(Model model) {	
		model.addAttribute("title", title);
		model.addAttribute("message", null);
		model.addAttribute("elements", dicoHelper.getDatatypeRepository());
		return "thdicodatatypes";
	}
	
	@PostMapping
	@PreAuthorize("hasRole('APP:dictionary-admin')")
	public String datatypesPOST(HttpServletRequest request, Model model) {	

		// Create entity
		OCDataType dataType = new OCDataType();
		dataType.setName(request.getParameter("name"));
		
		// Push entity to database		
		String message = null;
		try {
			dataType = datatypeRepository.saveAndFlush(dataType);
			message = "DataType " + dataType.getName() + " created";	
		} catch (Exception e) {
			message = "DataType " + dataType.getName() + " NOT created";
		}

		model.addAttribute("title", title);
		model.addAttribute("message", message);
		model.addAttribute("elements", dicoHelper.getDatatypeRepository());
		
		return "thdicodatatypes";
	}

	@RequestMapping("{datatypeName}/delete")
	@PreAuthorize("hasRole('APP:dictionary-admin')")
	public String datatypeDELETE(@PathVariable("datatypeName") String datatypeName, Model model) {
		
		OCDataType datatype = datatypeRepository.findOne(OCDataType.computeUrn(datatypeName));
		
		Collection<OCUnit> units = datatype.getUnits();
		String message;
		if (units.isEmpty()) {
			datatypeRepository.delete(datatype);
			message = "Datatype " + datatypeName + " deleted";
		} else {
			message = "Can't delete datatype " + datatypeName + ": it's used by at least one unit";
		}
		
		model.addAttribute("title", title);
		model.addAttribute("message", message);
		model.addAttribute("elements", dicoHelper.getDatatypeRepository());
		
		return "thdicodatatypes";
	}

	@RequestMapping("{datatypeName}")
	@PreAuthorize("hasRole('APP:dictionary-user')")
	public String datatypeDetailsGET(@PathVariable("datatypeName") String datatypeName, Model model) {
		
		OCDataType element = datatypeRepository.findOne(OCDataType.computeUrn(datatypeName));
		
		model.addAttribute("title", title);
		model.addAttribute("element", element);
		model.addAttribute("usedBy", element.getUnits());
		
		return "thdicodatatypedetails";
	}	
}
