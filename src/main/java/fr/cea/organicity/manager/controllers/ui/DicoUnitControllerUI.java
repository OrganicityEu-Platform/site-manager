package fr.cea.organicity.manager.controllers.ui;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.cea.organicity.manager.domain.OCAttributeType;
import fr.cea.organicity.manager.domain.OCUnit;
import fr.cea.organicity.manager.repositories.OCDataTypeRepository;
import fr.cea.organicity.manager.repositories.OCUnitRepository;
import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConstants;

@Controller
@RequestMapping("/dictionaries/units")
public class DicoUnitControllerUI implements Dico {
	
	@Autowired private DictionaryHelper dicoHelper;

	@Autowired private OCUnitRepository unitRepository;
	@Autowired private OCDataTypeRepository datatypeRepository;

	
	@RequestMapping
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public String unitsGET(HttpServletRequest request, Model model) {	
		model.addAttribute("title", title);
		model.addAttribute("message", null);
		model.addAttribute("isDicoAdmin", dicoHelper.isDictionaryAdmin(request));
		model.addAttribute("elements", dicoHelper.getUnitRepository());
		model.addAttribute("dataTypes", dicoHelper.getDatatypeRepository());
		
		return "thdicounits";
	}
	
	@PostMapping
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_ADMIN)
	public String unitPOST(HttpServletRequest request, Model model) {	

		OCUnit unit = new OCUnit();
		unit.setName(request.getParameter("name"));
		unit.setDatatype(datatypeRepository.findOne(request.getParameter("datatype")));
		unit.setDescription(request.getParameter("description"));
		unit.setRelated(request.getParameter("related"));
		
		String message = null;
		try {
			unit = unitRepository.saveAndFlush(unit);
			message = "Unit " + unit.getName() + " created";	
		} catch (Exception e) {
			message = "Unit " + unit.getName() + " NOT created";
		}
		
		model.addAttribute("title", title);
		model.addAttribute("message", message);
		model.addAttribute("isDicoAdmin", dicoHelper.isDictionaryAdmin(request));
		model.addAttribute("elements", dicoHelper.getUnitRepository());
		model.addAttribute("dataTypes", dicoHelper.getDatatypeRepository());
		
		return "thdicounits";
	}
	
	@RequestMapping("{unitName}/delete")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_ADMIN)
	public String unitDELETE(HttpServletRequest request, @PathVariable("unitName") String unitName, Model model) {

		OCUnit unit = unitRepository.findOne(OCUnit.computeUrn(unitName));
		
		Collection<OCAttributeType> attributes = unit.getAttributes();
		String message;
		if (attributes.isEmpty()) {
			unitRepository.delete(unit.getUrn());
			message = "Unit with name " + unitName + " deleted";
		} else {
			message = "Can't delete unit with name " + unitName + ": it's used by at least one attributes";
		}
		
		model.addAttribute("title", title);
		model.addAttribute("message", message);
		model.addAttribute("isDicoAdmin", dicoHelper.isDictionaryAdmin(request));
		model.addAttribute("elements", dicoHelper.getUnitRepository());
		model.addAttribute("dataTypes", dicoHelper.getDatatypeRepository());
		
		return "thdicounits";
	}
	
	@GetMapping("{unitName}")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public String unitDetailsGET(HttpServletRequest request, @PathVariable("unitName") String unitName, Model model) {
		OCUnit unit = unitRepository.findOne(OCUnit.computeUrn(unitName));
		
		model.addAttribute("title", title);
		model.addAttribute("isDicoAdmin", dicoHelper.isDictionaryAdmin(request));
		model.addAttribute("element", unit);
		model.addAttribute("usedBy", unit.getAttributes());
		
		return "thdicounitdetails";
	}	
	
	@PostMapping("{unitName}")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_ADMIN)
	public String unitDetailsPOST(HttpServletRequest request, @PathVariable("unitName") String unitName, Model model) {
		OCUnit unit = unitRepository.findOne(OCUnit.computeUrn(unitName));

		unit.setDescription(request.getParameter("description"));
		unit.setRelated(request.getParameter("related"));
		unit = unitRepository.save(unit);
		String message = "Update performed";
		
		model.addAttribute("title", title);
		model.addAttribute("message", message);
		model.addAttribute("isDicoAdmin", dicoHelper.isDictionaryAdmin(request));
		model.addAttribute("element", unit);
		model.addAttribute("usedBy", unit.getAttributes());
		
		return "thdicounitdetails";
	}
}
