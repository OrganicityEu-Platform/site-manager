package fr.cea.organicity.manager.template;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import fr.cea.organicity.manager.domain.OCAppType;
import fr.cea.organicity.manager.domain.OCAssetType;
import fr.cea.organicity.manager.domain.OCAttributeType;
import fr.cea.organicity.manager.domain.OCDataType;
import fr.cea.organicity.manager.domain.OCTool;
import fr.cea.organicity.manager.domain.OCUnit;
import fr.cea.organicity.manager.domain.OCUnregisteredAssetType;
import fr.cea.organicity.manager.security.Role;

public class Dictionaries {

	private static final String TITLE = "Dictionaries";

	public static String generateDictionaries(TemplateEngine templateService, List<Role> roles) throws IOException {
		String content = templateService.stringFromTemplate("/templates/dictionariesList.html");
		return templateService.generateWebPage(TITLE, content, roles);
	}

	public static String generateAssettypes(TemplateEngine templateService, List<Role> roles, String message, boolean isAdmin, List<OCAssetType> assetTypes, List<OCUnregisteredAssetType> unregisteredAssetTypes) throws IOException {
		String content = TemplateEngine.createNavigateLink("/dictionaries", "&lt; Back to dictionaries") + "\n";

		content += "<h2>AssetTypes list</h2>\n";
		content += TemplateEngine.generateMessageBannerIfNeeded(message);
		content += TemplateEngine.createListSize(assetTypes, "asset type");
		content += "<ul>\n";
		for (OCAssetType assetType : assetTypes) {
			content += "	<li>" + TemplateEngine.createNavigateLink("/dictionaries/assettypes/" + assetType.getName(), assetType.getUrn()) + "</li>\n";
		}
		content += "</ul>\n";

		
		if (isAdmin) {
			content += "<h2>New Asset Type</h2>\n";
			
			// suggestions
			if (! unregisteredAssetTypes.isEmpty()) {
				content += TemplateEngine.createListSize(unregisteredAssetTypes, "<strong>suggested for creation</strong> asset type");
				content += "<ul>\n";
				for (OCUnregisteredAssetType assetType : unregisteredAssetTypes) {
					content += "	<li><a href=\"#name\" onclick=\"suggest('" + assetType.getName() + "');\">" + assetType.getUrn() + "</a></li>\n";
				}
				content += "</ul>\n";
			}
			
			// form
			content += "<p>To add a new entity, please fill the following form</p>\n";
			content += "<form>\n";
			content += TemplateEngine.generateFormInput("name", "Name");
			content += TemplateEngine.generateFormInput("description", "Description");
			content += TemplateEngine.generateFormInput("related", "Related");
			content += "<div class=\"btnbar\">\n";
			content += TemplateEngine.generateSubmitBtn("newAssetType", "CREATE Asset Type", null, "name", "description", "related");
			content += "</div>\n";
			content += "</form>\n";
		}

		return templateService.generateWebPage(TITLE, content, roles);
	}
	
	public static String generateAssettypeDetail(TemplateEngine templateService, List<Role> roles, String message, boolean isAdmin, OCAssetType assetType, Iterable<OCAttributeType> attributes) throws IOException {
		String content = TemplateEngine.createNavigateLink("/dictionaries/assettypes", "&lt; Back to assettypes list") + "\n";
		content += "<h2>" + assetType.getName() + " assettype</h2>\n";
		content += TemplateEngine.generateMessageBannerIfNeeded(message);
		
		content += TemplateEngine.createKeyValue("Urn", assetType.getUrn());
		content += TemplateEngine.createKeyValue("Name", assetType.getName());

		if (isAdmin) {
			content += "<form class=\"editmode\">\n";
			content += TemplateEngine.generateFormInput("description", "Description", assetType.getDescription());
			content += TemplateEngine.generateFormInput("related", "Related", assetType.getRelated());
			content += "</form>\n";
		} 

		content += "<div class=\"displaymode\">\n";
		content += TemplateEngine.createKeyValue("Description", assetType.getDescription());
		content += TemplateEngine.createKeyValue("Related", assetType.getRelated());
		content += "</div>\n";

		if (isAdmin) {
			content += "<div class=\"btnbar editmode\">\n";
			content += TemplateEngine.generateSubmitBtn("updateAssetType", "UPDATE " + assetType.getName() + " Asset Type basic info", "'" + assetType.getName() + "'", "description", "related");
			content += "</div>\n";
		}

		content += TemplateEngine.createListSize(assetType.getAttributes(), "attribute");
		content += "<ul>\n";
		for (OCAttributeType attribute : assetType.getAttributes()) {
			content += "	<li>" + TemplateEngine.createNavigateLink("/dictionaries/attributetypes/" + attribute.getName(), attribute.getUrn());
			if (isAdmin) {
				String url = "/dictionaries/assettypes/" + assetType.getName() + "/removeattribute/" + attribute.getUrn();
				content += "&nbsp;&nbsp;<span class=\"inlineeditmode\" style=\"font-size:small;\">" + TemplateEngine.createNavigateLink(url, "[REMOVE]") + "</span>";
			}
			content += "</li>\n";
		}
		content += "</ul>\n";
		
		if (isAdmin) {
			content += "<div class=\"editmode\">\n";
			List<OCAttributeType> absent = StreamSupport.stream(attributes.spliterator(), false).
					filter(a -> !assetType.getAttributes().contains(a)).collect(Collectors.toList());
			
			String addBtn = TemplateEngine.generateSubmitBtn("addAttributeToAssetType", "ADD Attribute Type", "'" + assetType.getName() + "'", "newattribute");
			content += TemplateEngine.generateFormSelect("add attribute type", "newattribute", absent, OCAttributeType::getUrn, OCAttributeType::getUrn, addBtn);
			content += "</div>\n";
			
			content += "<div class=\"btnbar editmode\">\n";
			content += TemplateEngine.generateDeleteBtn("/dictionaries/assettypes", assetType.getName(), "assettype");
			content += "</div>\n";
			content += "<p class=\"displaymode\"><a href=\"#\" class=\"btn validate\" onclick=\"activateEditMode();\">activate edit mode</a></p>\n";
		}
		
		return templateService.generateWebPage(TITLE, content, roles);
	}
	
	public static String generateAttributetypes(TemplateEngine templateService, List<Role> roles, String message, boolean isAdmin, Iterable<OCAttributeType> attributeTypes) throws IOException {
		String content = TemplateEngine.createNavigateLink("/dictionaries", "&lt; Back to dictionaries") + "\n";
		content += "<h2>AttributeTypes list</h2>\n";
		content += TemplateEngine.generateMessageBannerIfNeeded(message);
		
		content += TemplateEngine.createListSize(attributeTypes, "attribute type");
		content += "<ul>\n";
		for (OCAttributeType attributeType : attributeTypes) {
			content += "	<li>" + TemplateEngine.createNavigateLink("/dictionaries/attributetypes/" + attributeType.getName(), attributeType.getUrn()) + "</li>\n";
		}
		content += "</ul>\n";

		if (isAdmin) {
			content += "<h2>New Attribute Type</h2>\n";
			content += "<p>To add a new entity, please fill the following form</p>\n";
			content += "<form>\n";
			content += TemplateEngine.generateFormInput("name", "Name");
			content += TemplateEngine.generateFormInput("description", "Description");
			content += TemplateEngine.generateFormInput("related", "Related");
			content += "<div class=\"btnbar\">\n";
			content += TemplateEngine.generateSubmitBtn("newAttributeType", "CREATE Attribute Type", null, "name", "description", "related");
			content += "</div>\n";
			content += "</form>\n";
		}
		
		return templateService.generateWebPage(TITLE, content, roles);
	}

	public static String generateAttributetypeDetail(TemplateEngine templateService, List<Role> roles, String message, boolean isAdmin, OCAttributeType attributeType, Iterable<OCUnit> units) throws IOException {
		String content = TemplateEngine.createNavigateLink("/dictionaries/attributetypes", "&lt; Back to attributetypes list") + "\n";
		content += "<h2>" + attributeType.getName() + " attributetype</h2>\n";
		content += TemplateEngine.generateMessageBannerIfNeeded(message);
		
		content += TemplateEngine.createKeyValue("Urn", attributeType.getUrn());
		content += TemplateEngine.createKeyValue("Name", attributeType.getName());
		
		if (isAdmin) {
			content += "<form class=\"editmode\">\n";
			content += TemplateEngine.generateFormInput("description", "Description", attributeType.getDescription());
			content += TemplateEngine.generateFormInput("related", "Related", attributeType.getRelated());
			content += "</form>\n";
		}
		
		content += "<div class=\"displaymode\">\n";
		content += TemplateEngine.createKeyValue("Description", attributeType.getDescription());
		content += TemplateEngine.createKeyValue("Related", attributeType.getRelated());
		content += "</div>\n";
		
		if (isAdmin) {
			content += "<div class=\"btnbar editmode\">\n";
			content += TemplateEngine.generateSubmitBtn("updateAttributeType", "UPDATE " + attributeType.getName() + " Attribute Type basic info", "'" + attributeType.getName() + "'", "description", "related");
			content += "</div>\n";
		}
		
		content += TemplateEngine.createListSize(attributeType.getUnits(), "unit");
		content += "<ul>\n";
		for (OCUnit unit : attributeType.getUnits()) {
			content += "	<li>" + TemplateEngine.createNavigateLink("/dictionaries/units/" + unit.getName(), unit.getUrn());
			if (isAdmin) {
				String url = "/dictionaries/attributetypes/" + attributeType.getName() + "/removeunit/" + unit.getUrn();
				content += "&nbsp;&nbsp;<span class=\"inlineeditmode\" style=\"font-size:small;\">" + TemplateEngine.createNavigateLink(url, "[REMOVE]") + "</span>";
			}
			content += "</li>\n";
		}
		content += "</ul>\n";

		if (isAdmin) {
			content += "<div class=\"editmode\">\n";
			List<OCUnit> absent = StreamSupport.stream(units.spliterator(), false).
					filter(u -> !attributeType.getUnits().contains(u)).collect(Collectors.toList());
			
			String addBtn = TemplateEngine.generateSubmitBtn("addUnitToAttributeType", "ADD Unit", "'" + attributeType.getName() + "'", "newunit");
			content += TemplateEngine.generateFormSelect("add unit", "newunit", absent, OCUnit::getUrn, OCUnit::getUrn, addBtn);
			content += "</div>\n";
		}
		
		content += TemplateEngine.generateUsedBy(attributeType.getAssets(), "Asset Type", "assettypes", OCAssetType::getName, OCAssetType::getUrn);
		
		if (isAdmin) {
			content += "<div class=\"btnbar editmode\">\n";
			content += TemplateEngine.generateDeleteBtn("/dictionaries/attributetypes", attributeType.getName(), "attributetype");
			content += "</div>\n";
			content += "<p class=\"displaymode\"><a href=\"#\" class=\"btn validate\" onclick=\"activateEditMode();\">activate edit mode</a></p>\n";
		}
		
		return templateService.generateWebPage(TITLE, content, roles);
	}
		
	public static String generateUnits(TemplateEngine templateService, List<Role> roles, String message, boolean isAdmin, Iterable<OCUnit> units, Iterable<OCDataType> dataTypes) throws IOException {
		
		String content = TemplateEngine.createNavigateLink("/dictionaries", "&lt; Back to dictionaries") + "\n";
		
		content += "<h2>Units list</h2>\n";
		content += TemplateEngine.generateMessageBannerIfNeeded(message);
		
		content += TemplateEngine.createListSize(units, "unit");
		content += "<ul>\n";
		for (OCUnit unit : units) {
			content += "	<li>" + TemplateEngine.createNavigateLink("/dictionaries/units/" + unit.getName(), unit.getUrn()) + "</li>\n";
		}
		content += "</ul>\n";

		if (isAdmin) {
			content += "<h2>New Unit</h2>\n";
			content += "<p>To add a new entity, please fill the following form</p>\n";
			content += "<form>\n";
			content += TemplateEngine.generateFormInput("name", "Name");
			content += TemplateEngine.generateFormSelect("datatype", "datatypes", dataTypes, OCDataType::getUrn, OCDataType::getUrn, null);
			content += TemplateEngine.generateFormInput("description", "Description");
			content += TemplateEngine.generateFormInput("related", "Related");			
			content += "<div class=\"btnbar\">\n";
			content += TemplateEngine.generateSubmitBtn("newUnit", "CREATE Unit", null, "name", "datatypes", "description", "related");
			content += "</div>\n";
			content += "</form>\n";
		}
				
		return templateService.generateWebPage(TITLE, content, roles);
	}

	public static String generateUnitDetail(TemplateEngine templateService, List<Role> roles, String message, boolean isAdmin, OCUnit unit) throws IOException {
		String content = TemplateEngine.createNavigateLink("/dictionaries/units", "&lt; Back to units list") + "\n";
		content += "<h2>" + unit.getName() + " unit</h2>\n";
		content += TemplateEngine.generateMessageBannerIfNeeded(message);
		
		content += TemplateEngine.createKeyValue("Urn", unit.getUrn());
		content += TemplateEngine.createKeyValue("Name", unit.getName());
		content += TemplateEngine.createKeyValue("Datatype", TemplateEngine.createNavigateLink("/dictionaries/datatypes/" + unit.getDatatype().getName(), unit.getDatatype().getUrn()));
		
		if (isAdmin) {
			content += "<form class=\"editmode\">\n";
			content += TemplateEngine.generateFormInput("description", "Description", unit.getDescription());
			content += TemplateEngine.generateFormInput("related", "Related", unit.getRelated());
			content += "</form>\n";
		}
		
		content += "<div class=\"displaymode\">\n";
		content += TemplateEngine.createKeyValue("Description", unit.getDescription());
		content += TemplateEngine.createKeyValue("Related", unit.getRelated());
		content += "</div>\n";
		
		content += TemplateEngine.generateUsedBy(unit.getAttributes(), "attribute", "attributetypes", OCAttributeType::getName, OCAttributeType::getUrn);
		
		if (isAdmin) {
			content += "<div class=\"btnbar editmode\">\n";
			content += TemplateEngine.generateSubmitBtn("updateUnit", "UPDATE " + unit.getName() + " unit", "'" + unit.getName() + "'", "description", "related");
			content += TemplateEngine.generateDeleteBtn("/dictionaries/units", unit.getName(), "unit");
			content += "</div>\n";
			content += "<p class=\"displaymode\"><a href=\"#\" class=\"btn validate\" onclick=\"activateEditMode();\">activate edit mode</a></p>\n";
		}
		
		return templateService.generateWebPage(TITLE, content, roles);
	}
	
	public static String generateDatatypes(TemplateEngine templateService, List<Role> roles, boolean isAdmin, String message, Iterable<OCDataType> datatypes) throws IOException {

		String content = TemplateEngine.createNavigateLink("/dictionaries", "&lt; Back to dictionaries") + "\n";
		content += "<h2>Data types list</h2>\n";
		content += TemplateEngine.generateMessageBannerIfNeeded(message);
		
		content += TemplateEngine.createListSize(datatypes, "datatype");
		content += "<ul>\n";
		for (OCDataType datatype : datatypes) {
			content += "	<li>" + TemplateEngine.createNavigateLink("/dictionaries/datatypes/" + datatype.getName(), datatype.getUrn())
					+ "</li>\n";
		}
		content += "</ul>\n";

		if (isAdmin) {
			content += "<h2>New Data Type</h2>\n";
			content += "<p>To add a new entity, please fill the following form</p>\n";
			content += "<form>\n";			
			content += TemplateEngine.generateFormInput("name", "Name");
			content += "<div class=\"btnbar\">\n";
			content += TemplateEngine.generateSubmitBtn("newDataType", "CREATE Data Type", null, "name");
			content += "</div>\n";
			content += "</form>\n";
		}
		
		return templateService.generateWebPage(TITLE, content, roles);
	}

	public static String generateDatatypeDetail(TemplateEngine templateService, List<Role> roles, boolean isAdmin, OCDataType datatype) throws IOException {

		String content = TemplateEngine.createNavigateLink("/dictionaries/datatypes", "&lt; Back to datatypes list") + "\n";
		content += "<h2>" + datatype.getName() + " datatype</h2>\n";

		content += TemplateEngine.createKeyValue("Urn", datatype.getUrn());
		content += TemplateEngine.createKeyValue("Name", datatype.getName());
		content += TemplateEngine.generateUsedBy(datatype.getUnits(), "unit", "units", OCUnit::getName, OCUnit::getUrn);
		
		if (isAdmin) {
			content += "<div class=\"btnbar\">\n";
			content += TemplateEngine.generateDeleteBtn("/dictionaries/datatypes", datatype.getName(), "datatype");
			content += "</div>\n";
		}
				
		return templateService.generateWebPage(TITLE, content, roles);
	}

	public static String generateTools(TemplateEngine templateService, List<Role> roles, Iterable<OCTool> tools) throws IOException {

		String content = TemplateEngine.createNavigateLink("/dictionaries", "&lt; Back to dictionaries") + "\n";
		content += "<h2>Tools list</h2>\n";

		content += TemplateEngine.createListSize(tools, "tool");
		content += "<ul>\n";
		for (OCTool tool : tools) {
			content += "	<li>\n";
			content += "		<strong>" + tool.getUrn() + "</strong><br/>\n";
			content += "		name: " + tool.getName() + "<br/>\n";
			content += "		description: " + tool.getDescription() + "<br/>\n";
			content += "		url: " + TemplateEngine.createPlainLink(tool.getUrl()) + "\n";
			content += "	</li>\n";
		}
		content += "</ul>\n";

		return templateService.generateWebPage(TITLE, content, roles);
	}

	public static String generateAppTypes(TemplateEngine templateService, List<Role> roles, Iterable<OCAppType> types) throws IOException {

		String content = TemplateEngine.createNavigateLink("/dictionaries", "&lt; Back to dictionaries") + "\n";
		content += "<h2>Application types list</h2>\n";

		content += TemplateEngine.createListSize(types, "application type");
		content += "<ul>\n";
		for (OCAppType type : types)
			content += "	" + TemplateEngine.createKeyValueLi(type.getUrn(), type.getDescription());
		content += "</ul>\n";

		return templateService.generateWebPage(TITLE, content, roles);
	}	
}
