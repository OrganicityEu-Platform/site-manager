package fr.cea.organicity.manager.controllers.ui;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.cea.organicity.manager.domain.OCAssetType;
import fr.cea.organicity.manager.domain.OCAttributeType;
import fr.cea.organicity.manager.domain.OCUnit;
import fr.cea.organicity.manager.repositories.OCAttributeTypeRepository;
import fr.cea.organicity.manager.repositories.OCUnitRepository;

@Controller
@RequestMapping("/dictionaries/attributetypes")
public class DicoAttributeTypeControllerUI implements Dico {
	
	@Autowired private DictionaryHelper dicoHelper;
	@Autowired private OCAttributeTypeRepository attributeTypeRepository;
	@Autowired private OCUnitRepository unitRepository;

	
	@RequestMapping
	@PreAuthorize("hasRole('APP:dictionary-user')")
	public String attributeTypesGET(Model model) {
		model.addAttribute("title", title);
		model.addAttribute("message", null);
		model.addAttribute("elements", dicoHelper.getAttributeTypeRepository());
		
		return "thdicoattributes";
	}	
	
	@PostMapping
	@PreAuthorize("hasRole('APP:dictionary-admin')")
	public String attributeTypesPOST(HttpServletRequest request, Model model) {	

		OCAttributeType attrType = new OCAttributeType();
		attrType.setName(request.getParameter("name"));
		attrType.setDescription(request.getParameter("description"));
		attrType.setRelated(request.getParameter("related"));
		
		String message = null;
		try {
			attrType = attributeTypeRepository.saveAndFlush(attrType);
			message = "AttributeType " + attrType.getName() + " created";	
		} catch (Exception e) {
			message = "AttributeType " + attrType.getName() + " NOT created";
		}
		
		model.addAttribute("title", title);
		model.addAttribute("message", message);
		model.addAttribute("elements", dicoHelper.getAttributeTypeRepository());
		
		return "thdicoattributes";
	}
	
	@RequestMapping("{attributetypeName}/delete")
	@PreAuthorize("hasRole('APP:dictionary-admin')")
	public String attributeTypeDELETE(@PathVariable("attributetypeName") String attributetypeName, Model model) {
		OCAttributeType attributeType = attributeTypeRepository.findOne(OCAttributeType.computeUrn(attributetypeName));

		Collection<OCAssetType> assets = attributeType.getAssets();
		
		String message;
		if (assets.isEmpty()) {
			attributeTypeRepository.delete(attributeType);
			message = "Attribute Type " + attributetypeName + " deleted";
		} else {
			message = "Can't delete attribute type with name " + attributetypeName + ": it's used by at least one asset type";
		}

		model.addAttribute("title", title);
		model.addAttribute("message", message);
		model.addAttribute("elements", dicoHelper.getAttributeTypeRepository());
		
		return "thdicoattributes";
	}
	
	@RequestMapping("{attributetypeName}")
	@PreAuthorize("hasRole('APP:dictionary-user')")
	public String attributeTypeDetailsGET(@PathVariable("attributetypeName") String attributetypeName, Model model) {
		OCAttributeType attributeType = attributeTypeRepository.findOne(OCAttributeType.computeUrn(attributetypeName));
	
		model.addAttribute("title", title);
		model.addAttribute("element", attributeType);
		model.addAttribute("units", attributeType.getUnits());
		model.addAttribute("allunits", unitRepository.findAll());
		model.addAttribute("usedBy", attributeType.getAssets());
		
		return "thdicoattributedetails";
	}

	@PostMapping("{attributetypeName}")
	@PreAuthorize("hasRole('APP:dictionary-admin')")
	public String attributeTypeDetailsPOST(HttpServletRequest request, @PathVariable("attributetypeName") String attributetypeName, Model model) {
		OCAttributeType attributeType = attributeTypeRepository.findOne(OCAttributeType.computeUrn(attributetypeName));

		String message = "Update performed";
		
		// update description attribute
		String description = request.getParameter("description");
		if (description != null)
			attributeType.setDescription(description);
		
		// update related attribute
		String related = request.getParameter("related");
		if (related != null)
			attributeType.setRelated(related);
		
		//update unit attribute
		String unitName = request.getParameter("unit");
		if (unitName != null) {
			Collection<OCUnit> units = attributeType.getUnits();
			OCUnit unit = unitRepository.findOne(unitName);
			if (unit == null) {
				message = "Can't find unit to be added";
			} else {
				units.add(unit);
			}			
			attributeType = attributeTypeRepository.save(attributeType);
		}
		
		model.addAttribute("title", title);
		model.addAttribute("message", message);
		model.addAttribute("element", attributeType);
		model.addAttribute("units", attributeType.getUnits());
		model.addAttribute("allunits", unitRepository.findAll());
		model.addAttribute("usedBy", attributeType.getAssets());
		
		return "thdicoattributedetails";
	}

	@RequestMapping("{attributetypeName}/removeunit/{unitName}")
	@PreAuthorize("hasRole('APP:dictionary-admin')")
	public String attributeTypeUnitREMOVE(@PathVariable("attributetypeName") String attributetypeName, @PathVariable("unitName") String unitName, Model model) {
		OCAttributeType attributeType = attributeTypeRepository.findOne(OCAttributeType.computeUrn(attributetypeName));
		
		String message;
		if (attributeType == null)
			message = "attributeType with name " + attributetypeName + " not found.";
		else {
			OCUnit unit = unitRepository.findOne(OCUnit.computeUrn(unitName));
			
			if (unit != null && attributeType.getUnits().remove(unit)) {
				attributeType = attributeTypeRepository.save(attributeType);
				message = "Unit " + unit.getUrn() + " removed from the list";
			} else {
				message = "Can't remove unit with name " + unitName + ": not found";
			}
		}
		
		model.addAttribute("title", title);
		model.addAttribute("message", message);
		model.addAttribute("element", attributeType);
		model.addAttribute("units", attributeType.getUnits());
		model.addAttribute("allunits", unitRepository.findAll());
		model.addAttribute("usedBy", attributeType.getAssets());
		
		return "thdicoattributedetails";
	}
}
