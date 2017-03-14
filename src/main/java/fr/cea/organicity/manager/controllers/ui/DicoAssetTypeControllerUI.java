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

import fr.cea.organicity.manager.domain.OCAssetType;
import fr.cea.organicity.manager.domain.OCAttributeType;
import fr.cea.organicity.manager.domain.OCUnregisteredAssetType;
import fr.cea.organicity.manager.repositories.OCAssetTypeRepository;
import fr.cea.organicity.manager.repositories.OCAttributeTypeRepository;
import fr.cea.organicity.manager.repositories.OCUnregisteredAssetTypeRepository;
import fr.cea.organicity.manager.security.RoleGuard;
import fr.cea.organicity.manager.security.SecurityConstants;

@Controller
@RequestMapping("/dictionaries/assettypes")
public class DicoAssetTypeControllerUI implements Dico {
	
	@Autowired private DictionaryHelper dicoHelper;
	@Autowired private OCAssetTypeRepository assetTypeRepository;
	@Autowired private OCUnregisteredAssetTypeRepository unregisteredassetTypeRepository;
	@Autowired private OCAttributeTypeRepository attributeTypeRepository;
	
	@GetMapping
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public String assettypesGET(HttpServletRequest request, Model model) {
		model.addAttribute("title", title);
		model.addAttribute("isDicoAdmin", dicoHelper.isDictionaryAdmin(request));
		model.addAttribute("elements", dicoHelper.getAssetTypeRepository());
		model.addAttribute("unregistered", dicoHelper.getUnregisteredAssetTypeRepository());
		
		return "thdicoassets";
	}
	
	@PostMapping
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_ADMIN)
	public String assettypesPOST(HttpServletRequest request, Model model) {	
		OCAssetType assetType = new OCAssetType();
		assetType.setName(request.getParameter("name"));
		assetType.setDescription(request.getParameter("description"));
		assetType.setRelated(request.getParameter("related"));

		String message = null;
		try {
			assetType = assetTypeRepository.saveAndFlush(assetType);
			try {
				unregisteredassetTypeRepository.delete(OCUnregisteredAssetType.computeUrn(request.getParameter("name")));
			} catch (Exception e) {	
				// do nothing
			}
			message = "AssetType " + assetType.getName() + " created";	
		} catch (Exception e) {
			message = "AssetType " + assetType.getName() + " NOT created";
		}
		
		model.addAttribute("title", title);
		model.addAttribute("message", message);
		model.addAttribute("isDicoAdmin", dicoHelper.isDictionaryAdmin(request));
		model.addAttribute("elements", dicoHelper.getAssetTypeRepository());
		model.addAttribute("unregistered", dicoHelper.getUnregisteredAssetTypeRepository());
		
		return "thdicoassets";
	}	
	
	@RequestMapping("{assettypeName}/delete")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_ADMIN)
	public String assettypeDELETE(HttpServletRequest request, @PathVariable("assettypeName") String assettypeName, Model model) {
		OCAssetType assetType = assetTypeRepository.findOne(OCAssetType.computeUrn(assettypeName));
		
		String message = null;
		try {
			assetTypeRepository.delete(assetType);
			message = "AssetType " + assetType.getName() + " deleted";	
		} catch (Exception e) {
			message = "AssetType " + assetType.getName() + " NOT deleted";
		}

		model.addAttribute("title", title);
		model.addAttribute("message", message);
		model.addAttribute("isDicoAdmin", dicoHelper.isDictionaryAdmin(request));
		model.addAttribute("elements", dicoHelper.getAssetTypeRepository());
		model.addAttribute("unregistered", dicoHelper.getUnregisteredAssetTypeRepository());
		
		return "thdicoassets";
	}
	
	@RequestMapping("{assettypeName}")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_USER)
	public String assetTypeDetailsGET(HttpServletRequest request, @PathVariable("assettypeName") String assettypeName, Model model) {
		OCAssetType assetType = assetTypeRepository.findOne(OCAssetType.computeUrn(assettypeName));
		
		model.addAttribute("title", title);
		model.addAttribute("isDicoAdmin", dicoHelper.isDictionaryAdmin(request));
		model.addAttribute("element", assetType);
		model.addAttribute("attributeTypes", dicoHelper.getAttributeTypeRepository());
		model.addAttribute("attributes", assetType.getAttributes());
		
		return "thdicoassetdetails";
	}
	
	@PostMapping(value="{assettypeName}")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_ADMIN)
	public String assetTypeDetailsPOST(HttpServletRequest request, @PathVariable("assettypeName") String assettypeName, Model model) {
		OCAssetType assetType = assetTypeRepository.findOne(OCAssetType.computeUrn(assettypeName));
		
		String message = "Update performed";
		
		// description
		String description = request.getParameter("description");
		if (description != null)
			assetType.setDescription(description);
		
		// related
		String related = request.getParameter("related");
		if (related != null)
			assetType.setRelated(related);
		
		// attribute
		String attributeName = request.getParameter("attribute");
		if (attributeName != null) {
			Collection<OCAttributeType> attributes = assetType.getAttributes();
			OCAttributeType attribute = attributeTypeRepository.findOne(attributeName);
			if (attribute == null) {
				message = "Can't find attribute to be added";
			} else {
				attributes.add(attribute);
			}
		}
		
		assetType = assetTypeRepository.save(assetType);

		model.addAttribute("title", title);
		model.addAttribute("message", message);
		model.addAttribute("isDicoAdmin", dicoHelper.isDictionaryAdmin(request));
		model.addAttribute("element", assetType);
		model.addAttribute("attributeTypes", dicoHelper.getAttributeTypeRepository());
		model.addAttribute("attributes", assetType.getAttributes());
		
		return "thdicoassetdetails";
	}
	
	@RequestMapping("{assettypeName}/removeattribute/{attributeName}")
	@RoleGuard(roleName=SecurityConstants.DICTIONARY_ADMIN)
	public String assetTypeAttributeREMOVE(HttpServletRequest request, @PathVariable("assettypeName") String assettypeName, @PathVariable("attributeName") String attributeName, Model model) {
		OCAssetType assetType = assetTypeRepository.findOne(OCAssetType.computeUrn(assettypeName));

		OCAttributeType attribute = attributeTypeRepository.findOne(OCAttributeType.computeUrn(attributeName));

		String message = "Can't remove attribute with name " + attributeName + ": not found";
		if (attribute != null && assetType.getAttributes().remove(attribute)) {
			assetType = assetTypeRepository.save(assetType);
			message = "Attribute Type " + attribute.getUrn() + " removed from the list";
		}

		model.addAttribute("title", title);
		model.addAttribute("message", message);
		model.addAttribute("isDicoAdmin", dicoHelper.isDictionaryAdmin(request));
		model.addAttribute("element", assetType);
		model.addAttribute("attributeTypes", dicoHelper.getAttributeTypeRepository());
		model.addAttribute("attributes", assetType.getAttributes());
		
		return "thdicoassetdetails";
	}
}
