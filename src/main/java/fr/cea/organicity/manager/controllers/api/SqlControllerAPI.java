package fr.cea.organicity.manager.controllers.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import fr.cea.organicity.manager.domain.OCAssetType;
import fr.cea.organicity.manager.domain.OCAttributeType;
import fr.cea.organicity.manager.domain.OCService;
import fr.cea.organicity.manager.domain.OCSite;
import fr.cea.organicity.manager.domain.OCUnit;
import fr.cea.organicity.manager.repositories.OCAppTypeRepository;
import fr.cea.organicity.manager.repositories.OCAssetTypeRepository;
import fr.cea.organicity.manager.repositories.OCAttributeTypeRepository;
import fr.cea.organicity.manager.repositories.OCDataTypeRepository;
import fr.cea.organicity.manager.repositories.OCServiceRepository;
import fr.cea.organicity.manager.repositories.OCSiteRepository;
import fr.cea.organicity.manager.repositories.OCToolRepository;
import fr.cea.organicity.manager.repositories.OCUnitRepository;
import fr.cea.organicity.manager.repositories.OCUnregisteredAssetTypeRepository;
import fr.cea.organicity.manager.repositories.OCUserInterestRepository;

@Controller
@CrossOrigin(origins = "*")
public class SqlControllerAPI {

	@Autowired OCUserInterestRepository interests;
	@Autowired OCUnregisteredAssetTypeRepository unregistered;
	@Autowired OCDataTypeRepository datatypes;
	@Autowired OCUnitRepository units;
	@Autowired OCToolRepository tools;
	@Autowired OCSiteRepository sites;
	@Autowired OCServiceRepository services;
	@Autowired OCAttributeTypeRepository attrtypes;
	@Autowired OCAssetTypeRepository assettypes;
	@Autowired OCAppTypeRepository apps;
	
	@GetMapping(value = "/dump.sql")
	public String getSwaggerFile(Model model) throws IOException {
		model.addAttribute("interests", interests.findAll());
		model.addAttribute("unregistered", unregistered.findAll());
		model.addAttribute("datatypes", datatypes.findAll());
		model.addAttribute("units", units.findAll());
		model.addAttribute("tools", tools.findAll());
		model.addAttribute("sites", sites.findAll());
		model.addAttribute("sitemanagers", getSiteManagers());
		model.addAttribute("services", services.findAll());
		model.addAttribute("servicemanagers", getServiceManagers());
		model.addAttribute("attrtypes", attrtypes.findAll());
		model.addAttribute("attrtypesunits", getAttrtypeUnit());
		model.addAttribute("assettypes", assettypes.findAll());
		model.addAttribute("assetattributes", getAssetAttributes());
		model.addAttribute("apps", apps.findAll());
		return "dump";
	}

	private List<Pair<String, String>> getSiteManagers() {
		List<Pair<String, String>> list = new ArrayList<>();
		for (OCSite site : sites.findAll()) {
			for (String manager : site.getManagers()) {
				list.add(new Pair<String, String>(site.getUrn(), manager));
			}
		}
		return list;
	}	

	private List<Pair<String, String>> getServiceManagers() {
		List<Pair<String, String>> list = new ArrayList<>();
		for (OCSite site : sites.findAll()) {
			for (OCService service : site.getServices()) {
				for (String manager : service.getManagers()) {
					list.add(new Pair<String, String>(service.getUrn(), manager));	
				}
			}
		}
		return list;
	}	
	
	private List<Pair<String, String>> getAttrtypeUnit() {
		List<Pair<String, String>> list = new ArrayList<>();
		for (OCAttributeType attrtype : attrtypes.findAll()) {
			for (OCUnit unit : attrtype.getUnits()) {
				list.add(new Pair<String, String>(attrtype.getUrn(), unit.getUrn()));
			}
		}
		return list;
	}
	
	private Object getAssetAttributes() {
		List<Pair<String, String>> list = new ArrayList<>();
		for (OCAssetType asset : assettypes.findAll()) {
			for (OCAttributeType attr : asset.getAttributes()) {
				list.add(new Pair<String, String>(asset.getUrn(), attr.getUrn()));
			}
		}
		return list;
	}	
}
