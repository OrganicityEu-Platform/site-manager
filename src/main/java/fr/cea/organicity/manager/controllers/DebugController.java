package fr.cea.organicity.manager.controllers;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.cea.organicity.manager.domain.OCAppType;
import fr.cea.organicity.manager.domain.OCAssetType;
import fr.cea.organicity.manager.domain.OCAttributeType;
import fr.cea.organicity.manager.domain.OCDataType;
import fr.cea.organicity.manager.domain.OCService;
import fr.cea.organicity.manager.domain.OCSite;
import fr.cea.organicity.manager.domain.OCTool;
import fr.cea.organicity.manager.domain.OCUnit;
import fr.cea.organicity.manager.repositories.OCAppTypeRepository;
import fr.cea.organicity.manager.repositories.OCAssetTypeRepository;
import fr.cea.organicity.manager.repositories.OCAttributeTypeRepository;
import fr.cea.organicity.manager.repositories.OCDataTypeRepository;
import fr.cea.organicity.manager.repositories.OCSiteRepository;
import fr.cea.organicity.manager.repositories.OCToolRepository;
import fr.cea.organicity.manager.repositories.OCUnitRepository;
import fr.cea.organicity.manager.template.TemplateEngine;

@CrossOrigin(origins = "*")
@RestController
public class DebugController {

	@Autowired private OCSiteRepository siterepository;
	@Autowired private OCDataTypeRepository datatyperepository;
	@Autowired private OCUnitRepository unitrepository;
	@Autowired private OCAttributeTypeRepository attributetyperepository;
	@Autowired private OCToolRepository toolrepository;
	@Autowired private OCAppTypeRepository apptyperepository;
	@Autowired private OCAssetTypeRepository assettyperepository;
	
	@Autowired private TemplateEngine templateEngine;
	
	@RequestMapping(value = "/debug/resources", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public String getQuota(@RequestHeader(value = "Authorization", required = false) String auth) {
		String content = "<resourceInfos xmlns=\"http://fr.cea.sensinact/resource\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://fr.cea.sensinact/resource ../../../schema/sensinact-resource.xsd\">\n";

		for (OCAttributeType attr : attributetyperepository.findAll()) {
			String serviceName = "attributes";
			String resourceName = attr.getName();
			Collection<OCUnit> units = attr.getUnits();
			if (units.size() == 0) {
				content += "\n<!-- " + resourceName + " has no unit -->\n";
			} else {
				OCUnit unit = units.iterator().next();
				String dataTypeName = unit.getDatatype().getName();
				if (dataTypeName.equals("numeric")) {
					content += generateAttribute(serviceName, resourceName, "java.lang.Double");
				} else {
					content += "\n<!-- unknown data type " + dataTypeName + " -->\n";
				}
			}
		}

		content += "\n</resourceInfos>\n";
		return content;
	}
	
	private String generateAttribute(String serviceName, String resourceName, String type) {
		String content = "\n	<resourceInfo xsi:type=\"resourceInfoSensor\" name=\"" + resourceName + "\" policy=\"SENSOR\" target=\"" + serviceName + "\" >\n";
		content += "		<identifier xsi:type=\"stringContent\">unused</identifier>\n";
		content += "		<type>" + type + "</type>\n";
		content += "	</resourceInfo>\n";

		return content;
	}
	
	
	@RequestMapping(value = "/debug/java", method = RequestMethod.GET)
	public String generateFile() throws IOException {
		String content = "	@RequestMapping\n	public String test() {\n";

		content += sitesContent();
		content += toolsContent();
		content += appTypeContent();
		content += dataTypeContent();
		content += assetTypeContent();
		content += unitContent();
		content += attributeContent();
		
		content += "		return \"{message: \\\"some data has been added to the database\\\"}\";\n";
		content += "	}\n";
		
		return content;
	}	

	private String sitesContent() throws IOException {
		String content = "\n"; 
		content += "		// SITES\n";
		content += "		// =====\n\n";
	
		for (OCSite site : siterepository.findAll()) {
			Map<String, String> dictionnary = new HashMap<>();
			String siteVar = "site_" + site.getName();
			dictionnary.put("siteVar", siteVar);
			dictionnary.put("name", site.getName());
			dictionnary.put("email", site.getEmail());
			dictionnary.put("related", site.getRelated());
			dictionnary.put("quota", Long.toString(site.getQuota()));
			dictionnary.put("remQuota", Long.toString(site.getRemQuota()));
			content += templateEngine.stringFromTemplate("/templates/java/site.txt", dictionnary) + "\n";
			
			for (OCService service : site.getServices()) {
				dictionnary.put("serviceVar", "srv_" + service.getName());
				dictionnary.put("name", service.getName());
				content += templateEngine.stringFromTemplate("/templates/java/service.txt", dictionnary) + "\n";
			}			
		}
		
		return content;
	}
	
	private String toolsContent() throws IOException {
		String content = "\n"; 
		content += "		// TOOLS\n";
		content += "		// =====\n\n";
	
		for (OCTool tool : toolrepository.findAll()) {
			Map<String, String> dictionnary = new HashMap<>();
			String toolVar = "tool_" + tool.getName();
			dictionnary.put("toolVar", toolVar);
			dictionnary.put("name", tool.getName());
			dictionnary.put("description", tool.getDescription());
			dictionnary.put("url", tool.getUrl());
			content += templateEngine.stringFromTemplate("/templates/java/tool.txt", dictionnary) + "\n";
		}
		
		return content;
	}
	
	private String appTypeContent() throws IOException {
		String content = "\n"; 
		content += "		// APP TYPE\n";
		content += "		// ========\n\n";
	
		for (OCAppType appType : apptyperepository.findAll()) {
			Map<String, String> dictionnary = new HashMap<>();			
			String appVar = "appType_" + appType.getName();
			dictionnary.put("var", appVar);
			dictionnary.put("name", appType.getName());
			dictionnary.put("description", appType.getDescription());
			content += templateEngine.stringFromTemplate("/templates/java/appType.txt", dictionnary) + "\n";
		}
		
		return content;
	}
	
	private String dataTypeContent() throws IOException {
		String content = "\n"; 
		content += "		// DATA TYPE\n";
		content += "		// =========\n\n";
	
		for (OCDataType dataType : datatyperepository.findAll()) {
			Map<String, String> dictionnary = new HashMap<>();
			String appVar = "dataType_" + dataType.getName();
			dictionnary.put("var", appVar);
			dictionnary.put("name", dataType.getName());
			content += templateEngine.stringFromTemplate("/templates/java/dataType.txt", dictionnary) + "\n";
		}
		
		return content;
	}
	
	private String assetTypeContent() throws IOException {
		String content = "\n"; 
		content += "		// ASSET TYPE\n";
		content += "		// ==========\n\n";
	
		for (OCAssetType assetType : assettyperepository.findAll()) {
			Map<String, String> dictionnary = new HashMap<>();
			String assetTypeVar = "assetType_" + assetType.getName().replaceAll(":", "_");
			dictionnary.put("var", assetTypeVar);
			dictionnary.put("name", assetType.getName());
			content += templateEngine.stringFromTemplate("/templates/java/assetType.txt", dictionnary) + "\n";
		}
		
		return content;
	}
	
	private String unitContent() throws IOException {
		String content = "\n"; 
		content += "		// UNIT\n";
		content += "		// ====\n\n";
	
		for (OCUnit unit : unitrepository.findAll()) {
			Map<String, String> dictionnary = new HashMap<>();
			String unitVar = "u_" + unit.getName();
			dictionnary.put("var", unitVar);
			dictionnary.put("name", unit.getName());
			dictionnary.put("dataType", "dataType_" + unit.getDatatype().getName());
			content += templateEngine.stringFromTemplate("/templates/java/unit.txt", dictionnary) + "\n";
		}
		
		return content;
	}
	
	private String attributeContent() throws IOException {
		String content = "\n"; 
		content += "		// ATTRIBUTE TYPE\n";
		content += "		// ==============\n\n";
	
		for (OCAttributeType attrType : attributetyperepository.findAll()) {
			String atVar = "attrType_" + attrType.getName().replaceAll(":", "_");
			
			content += "		OCAttributeType " + atVar + " = new OCAttributeType();\n";
			content += "		" + atVar + ".setName(\"" + attrType.getName() + "\");\n";			
			for (OCUnit unit : attrType.getUnits()) {
				content += "		" + atVar + ".getUnits().add(" + "u_" + unit.getName() + ");\n";
			}
			content += "		" + atVar + " = attributetyperepository.save(" + atVar + ");\n\n";			
		}
		
		return content;
	}	
}
