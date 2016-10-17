package fr.cea.organicity.manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import fr.cea.organicity.manager.domain.OCUnregisteredAssetType;
import fr.cea.organicity.manager.repositories.OCAppTypeRepository;
import fr.cea.organicity.manager.repositories.OCAssetTypeRepository;
import fr.cea.organicity.manager.repositories.OCAttributeTypeRepository;
import fr.cea.organicity.manager.repositories.OCDataTypeRepository;
import fr.cea.organicity.manager.repositories.OCServiceRepository;
import fr.cea.organicity.manager.repositories.OCSiteRepository;
import fr.cea.organicity.manager.repositories.OCToolRepository;
import fr.cea.organicity.manager.repositories.OCUnitRepository;
import fr.cea.organicity.manager.repositories.OCUnregisteredAssetTypeRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/test", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
public class TestController {

	@Autowired private OCSiteRepository siterepository;
	@Autowired private OCServiceRepository servicerepository;
	@Autowired private OCDataTypeRepository datatyperepository;
	@Autowired private OCUnitRepository unitrepository;
	@Autowired private OCAttributeTypeRepository attributetyperepository;
	@Autowired private OCToolRepository toolrepository;
	@Autowired private OCAppTypeRepository apptyperepository;
	@Autowired private OCUnregisteredAssetTypeRepository unregisteredassettyperepository;
	@Autowired private OCAssetTypeRepository assettyperepository;
	
	@RequestMapping
	public String test() {
		addSites();
		addTools();
		addAppTypes();
		addDataTypes();
		addAssetTypes();
		addUnregisteredAssetTypes();
		miscGenerated();
		return "{message: \"some data has been added to the database\"}";
	}

	@RequestMapping("/delta")
	public String addDelta() {
		return "{message: \"no delta available\"}";
		//return "{message: \"delta added\"}";
	}
	
	private void addSites() {
		OCSite aarhus = new OCSite();
		aarhus.setName("aarhus");
		aarhus.setEmail("contact@aarhus.dk");
		aarhus.setRelated("Aarhus is...");
		aarhus.setQuota(1000);
		aarhus.setRemQuota(1000);
		aarhus = siterepository.save(aarhus);
		
		OCService aarhus_service = new OCService();
		aarhus_service.setName("aarhusservice1");
		aarhus_service.setSite(aarhus);
		aarhus_service = servicerepository.save(aarhus_service);
		
		OCSite london = new OCSite();
		london.setName("london");
		london.setEmail("contact@london.uk");
		london.setRelated("london is...");
		london.setQuota(1000);
		london.setRemQuota(1000);
		london = siterepository.save(london);
				
		OCService london_service = new OCService();
		london_service.setName("londonservice1");
		london_service.setSite(london);
		london_service = servicerepository.save(london_service);
		
		OCSite santander = new OCSite();
		santander.setName("santander");
		santander.setEmail("contact@santander.es");
		santander.setRelated("santander is...");
		santander.setQuota(1000);
		santander.setRemQuota(1000);
		santander = siterepository.save(santander);
		
		OCService santander_service = new OCService();
		santander_service.setName("santanderservice1");
		santander_service.setSite(santander);
		santander_service = servicerepository.save(santander_service);
		
		OCSite provider = new OCSite();
		provider.setName("provider");
		provider.setEmail("contact@provider.dk");
		provider.setRelated("provider is...");
		provider.setQuota(1000);
		provider.setRemQuota(1000);
		provider = siterepository.save(provider);
		
		OCService provider_service = new OCService();
		provider_service.setName("provider");
		provider_service.setSite(provider);
		provider_service = servicerepository.save(provider_service);

		OCSite experimenter = new OCSite();
		experimenter.setName("experimenters");
		experimenter.setEmail("contact@experimenters.dk");
		experimenter.setRelated("experimenters is...");
		experimenter.setQuota(1000);
		experimenter.setRemQuota(1000);
		experimenter = siterepository.save(experimenter);
		
		OCService experimenter_service = new OCService();
		experimenter_service.setName("experimenter");
		experimenter_service.setSite(experimenter);
		experimenter_service = servicerepository.save(experimenter_service);
	}
		
	private void addTools() {

		OCTool eventbus = new OCTool();
		eventbus.setName("EventBus");
		eventbus.setUrl("https://organicityeu.github.io/EventBus/");
		eventbus.setDescription("eventbus is a nice tool");
		eventbus = toolrepository.save(eventbus);

		OCTool sna = new OCTool();
		sna.setName("SensiNact");
		sna.setUrl("https://organicityeu.github.io/tools/sensinact/");
		sna.setDescription("sensinact is a nice tool");
		sna = toolrepository.save(sna);

		OCTool websocket = new OCTool();
		websocket.setName("WebSocket");
		websocket.setUrl("https://github.com/OrganicityEu/Processing-Websocket-Library");
		websocket.setDescription("websocket is a nice tool");
		websocket = toolrepository.save(websocket);

		OCTool smartphone_expe = new OCTool();
		smartphone_expe.setName("SmartphoneExperiment");
		smartphone_expe.setUrl("https://organicityeu.github.io/set.html");
		smartphone_expe.setDescription("smartphone experiment is a nice tool");
		smartphone_expe = toolrepository.save(smartphone_expe);

		OCTool tst = new OCTool();
		tst.setName("TSmarT");
		tst.setUrl("https://organicityeu.github.io/tools/TSmarT/");
		tst.setDescription("TSmarT is a nice tool");
		tst = toolrepository.save(tst);

		OCTool tinkerspace = new OCTool();
		tinkerspace.setName("TinkerSpace");
		tinkerspace.setUrl("https://organicityeu.github.io/TinkerSpace/");
		tinkerspace.setDescription("tinkerspace is a nice tool");
		tinkerspace = toolrepository.save(tinkerspace);
	}
	
	private void addAppTypes() {
		OCAppType desktop = new OCAppType();
		desktop.setName("desktop");
		desktop.setDescription("desktop application type");
		desktop = apptyperepository.save(desktop);

		OCAppType mobile = new OCAppType();
		mobile.setName("mobile");
		mobile.setDescription("mobile application type");
		mobile = apptyperepository.save(mobile);

		OCAppType web = new OCAppType();
		web.setName("web");
		web.setDescription("web application type");
		web = apptyperepository.save(web);

		OCAppType smartphoneExperiment = new OCAppType();
		smartphoneExperiment.setName("smartphone");
		smartphoneExperiment.setDescription("smartphone experiment type");
		smartphoneExperiment = apptyperepository.save(smartphoneExperiment);
	}
	
	private void addDataTypes() {
		
		OCDataType url = new OCDataType();
		url.setName("url");
		url = datatyperepository.save(url);
	}
	
	private void addAssetTypes() {
		OCAssetType assettype_districtprofile = new OCAssetType();
		assettype_districtprofile.setName("districtprofile");
		assettype_districtprofile = assettyperepository.save(assettype_districtprofile);

		OCAssetType assettype_iotdevice = new OCAssetType();
		assettype_iotdevice.setName("iotdevice");
		assettype_iotdevice = assettyperepository.save(assettype_iotdevice);

		OCAssetType assettype_weatherstation = new OCAssetType();
		assettype_weatherstation.setName("weatherstation");
		assettype_weatherstation = assettyperepository.save(assettype_weatherstation);

		OCAssetType assettype_trafficstats = new OCAssetType();
		assettype_trafficstats.setName("trafficstats");
		assettype_trafficstats = assettyperepository.save(assettype_trafficstats);

		OCAssetType assettype_busstation = new OCAssetType();
		assettype_busstation.setName("busstation");
		assettype_busstation = assettyperepository.save(assettype_busstation);

		OCAssetType assettype_trainstation = new OCAssetType();
		assettype_trainstation.setName("trainstation");
		assettype_trainstation = assettyperepository.save(assettype_trainstation);

		OCAssetType assettype_tramstation = new OCAssetType();
		assettype_tramstation.setName("tramstation");
		assettype_tramstation = assettyperepository.save(assettype_tramstation);

		OCAssetType assettype_undergroundstation = new OCAssetType();
		assettype_undergroundstation.setName("undergroundstation");
		assettype_undergroundstation = assettyperepository.save(assettype_undergroundstation);

		OCAssetType assettype_environmentalstation = new OCAssetType();
		assettype_environmentalstation.setName("environmentalstation");
		assettype_environmentalstation = assettyperepository.save(assettype_environmentalstation);

		OCAssetType assettype_shelter = new OCAssetType();
		assettype_shelter.setName("shelter");
		assettype_shelter = assettyperepository.save(assettype_shelter);

		OCAssetType assettype_bikeStation = new OCAssetType();
		assettype_bikeStation.setName("bikeStation");
		assettype_bikeStation = assettyperepository.save(assettype_bikeStation);

		OCAssetType assettype_shop = new OCAssetType();
		assettype_shop.setName("shop");
		assettype_shop = assettyperepository.save(assettype_shop);

		OCAssetType assettype_busLine = new OCAssetType();
		assettype_busLine.setName("busLine");
		assettype_busLine = assettyperepository.save(assettype_busLine);

		OCAssetType assettype_urn_oc_entityTyoe_busRoute = new OCAssetType();
		assettype_urn_oc_entityTyoe_busRoute.setName("urn:oc:entityTyoe:busRoute");
		assettype_urn_oc_entityTyoe_busRoute = assettyperepository.save(assettype_urn_oc_entityTyoe_busRoute);

		OCAssetType assettype_taxiStation = new OCAssetType();
		assettype_taxiStation.setName("taxiStation");
		assettype_taxiStation = assettyperepository.save(assettype_taxiStation);

		OCAssetType assettype_district = new OCAssetType();
		assettype_district.setName("district");
		assettype_district = assettyperepository.save(assettype_district);

		OCAssetType assettype_section = new OCAssetType();
		assettype_section.setName("section");
		assettype_section = assettyperepository.save(assettype_section);

		OCAssetType assettype_event_culture = new OCAssetType();
		assettype_event_culture.setName("event:culture");
		assettype_event_culture = assettyperepository.save(assettype_event_culture);

		OCAssetType assettype_event_strike = new OCAssetType();
		assettype_event_strike.setName("event:strike");
		assettype_event_strike = assettyperepository.save(assettype_event_strike);

		OCAssetType assettype_iotdevice_luminosity = new OCAssetType();
		assettype_iotdevice_luminosity.setName("iotdevice:luminosity");
		assettype_iotdevice_luminosity = assettyperepository.save(assettype_iotdevice_luminosity);

		OCAssetType assettype_iotdevice_noise = new OCAssetType();
		assettype_iotdevice_noise.setName("iotdevice:noise");
		assettype_iotdevice_noise = assettyperepository.save(assettype_iotdevice_noise);

		OCAssetType assettype_iotdevice_vehiclecount = new OCAssetType();
		assettype_iotdevice_vehiclecount.setName("iotdevice:vehiclecount");
		assettype_iotdevice_vehiclecount = assettyperepository.save(assettype_iotdevice_vehiclecount);

		OCAssetType assettype_iotdevice_vehiclespeed = new OCAssetType();
		assettype_iotdevice_vehiclespeed.setName("iotdevice:vehiclespeed");
		assettype_iotdevice_vehiclespeed = assettyperepository.save(assettype_iotdevice_vehiclespeed);

		OCAssetType assettype_iotdevice_agriculture = new OCAssetType();
		assettype_iotdevice_agriculture.setName("iotdevice:agriculture");
		assettype_iotdevice_agriculture = assettyperepository.save(assettype_iotdevice_agriculture);

		OCAssetType assettype_iotdevice_irrigation = new OCAssetType();
		assettype_iotdevice_irrigation.setName("iotdevice:irrigation");
		assettype_iotdevice_irrigation = assettyperepository.save(assettype_iotdevice_irrigation);

		OCAssetType assettype_iotdevice_magneticLoop = new OCAssetType();
		assettype_iotdevice_magneticLoop.setName("iotdevice:magneticLoop");
		assettype_iotdevice_magneticLoop = assettyperepository.save(assettype_iotdevice_magneticLoop);

		OCAssetType assettype_trafficStatus = new OCAssetType();
		assettype_trafficStatus.setName("trafficStatus");
		assettype_trafficStatus = assettyperepository.save(assettype_trafficStatus);
	}
	
	private void addUnregisteredAssetTypes() {
		OCUnregisteredAssetType mySuggestion = new OCUnregisteredAssetType();
		mySuggestion.setName("mySuggestion");
		mySuggestion = unregisteredassettyperepository.save(mySuggestion);
	}
	
	private void miscGenerated() {

		// data type dictionary
		// ====================
		
		OCDataType dt_numeric = new OCDataType();
		dt_numeric.setName("numeric");
		dt_numeric = datatyperepository.save(dt_numeric);

		
		// unit dictionary
		// ===============
		
		OCUnit u_bar = new OCUnit();
		u_bar.setName("bar");
		u_bar.setDatatype(dt_numeric);
		u_bar = unitrepository.save(u_bar);

		OCUnit u_centibar = new OCUnit();
		u_centibar.setName("centibar");
		u_centibar.setDatatype(dt_numeric);
		u_centibar = unitrepository.save(u_centibar);

		OCUnit u_decibel = new OCUnit();
		u_decibel.setName("decibel");
		u_decibel.setDatatype(dt_numeric);
		u_decibel = unitrepository.save(u_decibel);

		OCUnit u_degreeAngle = new OCUnit();
		u_degreeAngle.setName("degreeAngle");
		u_degreeAngle.setDatatype(dt_numeric);
		u_degreeAngle = unitrepository.save(u_degreeAngle);

		OCUnit u_degreeCelsius = new OCUnit();
		u_degreeCelsius.setName("degreeCelsius");
		u_degreeCelsius.setDatatype(dt_numeric);
		u_degreeCelsius = unitrepository.save(u_degreeCelsius);

		OCUnit u_index = new OCUnit();
		u_index.setName("index");
		u_index.setDatatype(dt_numeric);
		u_index = unitrepository.save(u_index);

		OCUnit u_kilogram = new OCUnit();
		u_kilogram.setName("kilogram");
		u_kilogram.setDatatype(dt_numeric);
		u_kilogram = unitrepository.save(u_kilogram);

		OCUnit u_kilometrePerHour = new OCUnit();
		u_kilometrePerHour.setName("kilometrePerHour");
		u_kilometrePerHour.setDatatype(dt_numeric);
		u_kilometrePerHour = unitrepository.save(u_kilometrePerHour);

		OCUnit u_kilometre = new OCUnit();
		u_kilometre.setName("kilometre");
		u_kilometre.setDatatype(dt_numeric);
		u_kilometre = unitrepository.save(u_kilometre);

		OCUnit u_litrePer100Kilometres = new OCUnit();
		u_litrePer100Kilometres.setName("litrePer100Kilometres");
		u_litrePer100Kilometres.setDatatype(dt_numeric);
		u_litrePer100Kilometres = unitrepository.save(u_litrePer100Kilometres);

		OCUnit u_litre = new OCUnit();
		u_litre.setName("litre");
		u_litre.setDatatype(dt_numeric);
		u_litre = unitrepository.save(u_litre);

		OCUnit u_lumen = new OCUnit();
		u_lumen.setName("lumen");
		u_lumen.setDatatype(dt_numeric);
		u_lumen = unitrepository.save(u_lumen);

		OCUnit u_lux = new OCUnit();
		u_lux.setName("lux");
		u_lux.setDatatype(dt_numeric);
		u_lux = unitrepository.save(u_lux);

		OCUnit u_metre = new OCUnit();
		u_metre.setName("metre");
		u_metre.setDatatype(dt_numeric);
		u_metre = unitrepository.save(u_metre);

		OCUnit u_microgramPerCubicMetre = new OCUnit();
		u_microgramPerCubicMetre.setName("microgramPerCubicMetre");
		u_microgramPerCubicMetre.setDatatype(dt_numeric);
		u_microgramPerCubicMetre = unitrepository.save(u_microgramPerCubicMetre);

		OCUnit u_milligramPerCubicMetre = new OCUnit();
		u_milligramPerCubicMetre.setName("milligramPerCubicMetre");
		u_milligramPerCubicMetre.setDatatype(dt_numeric);
		u_milligramPerCubicMetre = unitrepository.save(u_milligramPerCubicMetre);

		OCUnit u_millimetrePerHour = new OCUnit();
		u_millimetrePerHour.setName("millimetrePerHour");
		u_millimetrePerHour.setDatatype(dt_numeric);
		u_millimetrePerHour = unitrepository.save(u_millimetrePerHour);

		OCUnit u_millivoltPerMetre = new OCUnit();
		u_millivoltPerMetre.setName("millivoltPerMetre");
		u_millivoltPerMetre.setDatatype(dt_numeric);
		u_millivoltPerMetre = unitrepository.save(u_millivoltPerMetre);

		OCUnit u_percent = new OCUnit();
		u_percent.setName("percent");
		u_percent.setDatatype(dt_numeric);
		u_percent = unitrepository.save(u_percent);

		OCUnit u_revolutionPerMinute = new OCUnit();
		u_revolutionPerMinute.setName("revolutionPerMinute");
		u_revolutionPerMinute.setDatatype(dt_numeric);
		u_revolutionPerMinute = unitrepository.save(u_revolutionPerMinute);

		OCUnit u_vehiclePerMinute = new OCUnit();
		u_vehiclePerMinute.setName("vehiclePerMinute");
		u_vehiclePerMinute.setDatatype(dt_numeric);
		u_vehiclePerMinute = unitrepository.save(u_vehiclePerMinute);

		OCUnit u_wattPerSquareMetre = new OCUnit();
		u_wattPerSquareMetre.setName("wattPerSquareMetre");
		u_wattPerSquareMetre.setDatatype(dt_numeric);
		u_wattPerSquareMetre = unitrepository.save(u_wattPerSquareMetre);

		
		// attribute type
		// ==============
		
		OCAttributeType attr_type_batteryLevel = new OCAttributeType();
		attr_type_batteryLevel.setName("batteryLevel");
		attr_type_batteryLevel.getUnits().add(u_percent);
		attr_type_batteryLevel = attributetyperepository.save(attr_type_batteryLevel);

		OCAttributeType attr_type_chemicalAgentAtmosphericConcentration_airParticles = new OCAttributeType();
		attr_type_chemicalAgentAtmosphericConcentration_airParticles
				.setName("chemicalAgentAtmosphericConcentration:airParticles");
		attr_type_chemicalAgentAtmosphericConcentration_airParticles.getUnits()
				.add(u_milligramPerCubicMetre);
		attr_type_chemicalAgentAtmosphericConcentration_airParticles = attributetyperepository.save(attr_type_chemicalAgentAtmosphericConcentration_airParticles);

		OCAttributeType attr_type_chemicalAgentAtmosphericConcentration_CO = new OCAttributeType();
		attr_type_chemicalAgentAtmosphericConcentration_CO
				.setName("chemicalAgentAtmosphericConcentration:CO");
		attr_type_chemicalAgentAtmosphericConcentration_CO.getUnits().add(u_milligramPerCubicMetre);
		attr_type_chemicalAgentAtmosphericConcentration_CO = attributetyperepository.save(attr_type_chemicalAgentAtmosphericConcentration_CO);

		OCAttributeType attr_type_chemicalAgentAtmosphericConcentration_NO2 = new OCAttributeType();
		attr_type_chemicalAgentAtmosphericConcentration_NO2
				.setName("chemicalAgentAtmosphericConcentration:NO2");
		attr_type_chemicalAgentAtmosphericConcentration_NO2.getUnits().add(u_microgramPerCubicMetre);
		attr_type_chemicalAgentAtmosphericConcentration_NO2 = attributetyperepository.save(attr_type_chemicalAgentAtmosphericConcentration_NO2);

		OCAttributeType attr_type_chemicalAgentAtmosphericConcentration_O3 = new OCAttributeType();
		attr_type_chemicalAgentAtmosphericConcentration_O3
				.setName("chemicalAgentAtmosphericConcentration:O3");
		attr_type_chemicalAgentAtmosphericConcentration_O3.getUnits().add(u_microgramPerCubicMetre);
		attr_type_chemicalAgentAtmosphericConcentration_O3 = attributetyperepository.save(attr_type_chemicalAgentAtmosphericConcentration_O3);

		OCAttributeType attr_type_chemicalAgentAtmosphericConcentration_SO2 = new OCAttributeType();
		attr_type_chemicalAgentAtmosphericConcentration_SO2
				.setName("chemicalAgentAtmosphericConcentration:SO2");
		attr_type_chemicalAgentAtmosphericConcentration_SO2.getUnits().add(u_microgramPerCubicMetre);
		attr_type_chemicalAgentAtmosphericConcentration_SO2 = attributetyperepository.save(attr_type_chemicalAgentAtmosphericConcentration_SO2);

		OCAttributeType attr_type_direction_azimuth = new OCAttributeType();
		attr_type_direction_azimuth.setName("direction:azimuth");
		attr_type_direction_azimuth.getUnits().add(u_degreeAngle);
		attr_type_direction_azimuth = attributetyperepository.save(attr_type_direction_azimuth);

		OCAttributeType attr_type_direction_heading = new OCAttributeType();
		attr_type_direction_heading.setName("direction:heading");
		attr_type_direction_heading.getUnits().add(u_index);
		attr_type_direction_heading = attributetyperepository.save(attr_type_direction_heading);

		OCAttributeType attr_type_electricField_1800mhz = new OCAttributeType();
		attr_type_electricField_1800mhz.setName("electricField:1800mhz");
		attr_type_electricField_1800mhz.getUnits().add(u_millivoltPerMetre);
		attr_type_electricField_1800mhz = attributetyperepository.save(attr_type_electricField_1800mhz);

		OCAttributeType attr_type_electricField_2100mhz = new OCAttributeType();
		attr_type_electricField_2100mhz.setName("electricField:2100mhz");
		attr_type_electricField_2100mhz.getUnits().add(u_millivoltPerMetre);
		attr_type_electricField_2100mhz = attributetyperepository.save(attr_type_electricField_2100mhz);

		OCAttributeType attr_type_electricField_2400mhz = new OCAttributeType();
		attr_type_electricField_2400mhz.setName("electricField:2400mhz");
		attr_type_electricField_2400mhz.getUnits().add(u_millivoltPerMetre);
		attr_type_electricField_2400mhz = attributetyperepository.save(attr_type_electricField_2400mhz);

		OCAttributeType attr_type_electricField_900mhz = new OCAttributeType();
		attr_type_electricField_900mhz.setName("electricField:900mhz");
		attr_type_electricField_900mhz.getUnits().add(u_millivoltPerMetre);
		attr_type_electricField_900mhz = attributetyperepository.save(attr_type_electricField_900mhz);

		OCAttributeType attr_type_fillLevel_wasteContainer = new OCAttributeType();
		attr_type_fillLevel_wasteContainer.setName("fillLevel:wasteContainer");
		attr_type_fillLevel_wasteContainer.getUnits().add(u_percent);
		attr_type_fillLevel_wasteContainer = attributetyperepository.save(attr_type_fillLevel_wasteContainer);

		OCAttributeType attr_type_fuelConsumption_instantaneous = new OCAttributeType();
		attr_type_fuelConsumption_instantaneous.setName("fuelConsumption:instantaneous");
		attr_type_fuelConsumption_instantaneous.getUnits().add(u_litrePer100Kilometres);
		attr_type_fuelConsumption_instantaneous = attributetyperepository.save(attr_type_fuelConsumption_instantaneous);

		OCAttributeType attr_type_fuelConsumption_total = new OCAttributeType();
		attr_type_fuelConsumption_total.setName("fuelConsumption:total");
		attr_type_fuelConsumption_total.getUnits().add(u_litre);
		attr_type_fuelConsumption_total = attributetyperepository.save(attr_type_fuelConsumption_total);

		OCAttributeType attr_type_illuminance = new OCAttributeType();
		attr_type_illuminance.setName("illuminance");
		attr_type_illuminance.getUnits().add(u_lux);
		attr_type_illuminance = attributetyperepository.save(attr_type_illuminance);

		OCAttributeType attr_type_luminosity = new OCAttributeType();
		attr_type_luminosity.setName("luminosity");
		attr_type_luminosity.getUnits().add(u_lumen);
		attr_type_luminosity = attributetyperepository.save(attr_type_luminosity);

		OCAttributeType attr_type_mass = new OCAttributeType();
		attr_type_mass.setName("mass");
		attr_type_mass.getUnits().add(u_kilogram);
		attr_type_mass = attributetyperepository.save(attr_type_mass);

		OCAttributeType attr_type_mileage_distanceToService = new OCAttributeType();
		attr_type_mileage_distanceToService.setName("mileage:distanceToService");
		attr_type_mileage_distanceToService.getUnits().add(u_kilometre);
		attr_type_mileage_distanceToService = attributetyperepository.save(attr_type_mileage_distanceToService);

		OCAttributeType attr_type_mileage_total = new OCAttributeType();
		attr_type_mileage_total.setName("mileage:total");
		attr_type_mileage_total.getUnits().add(u_metre);
		attr_type_mileage_total = attributetyperepository.save(attr_type_mileage_total);

		OCAttributeType attr_type_motionState_vehicle = new OCAttributeType();
		attr_type_motionState_vehicle.setName("motionState:vehicle");
		attr_type_motionState_vehicle.getUnits().add(u_index);
		attr_type_motionState_vehicle = attributetyperepository.save(attr_type_motionState_vehicle);

		OCAttributeType attr_type_odometer = new OCAttributeType();
		attr_type_odometer.setName("odometer");
		attr_type_odometer.getUnits().add(u_kilometre);
		attr_type_odometer = attributetyperepository.save(attr_type_odometer);

		OCAttributeType attr_type_position_altitude = new OCAttributeType();
		attr_type_position_altitude.setName("position:altitude");
		attr_type_position_altitude.getUnits().add(u_metre);
		attr_type_position_altitude = attributetyperepository.save(attr_type_position_altitude);

		OCAttributeType attr_type_position_latitude = new OCAttributeType();
		attr_type_position_latitude.setName("position:latitude");
		attr_type_position_latitude.getUnits().add(u_degreeAngle);
		attr_type_position_latitude = attributetyperepository.save(attr_type_position_latitude);

		OCAttributeType attr_type_position_longitude = new OCAttributeType();
		attr_type_position_longitude.setName("position:longitude");
		attr_type_position_longitude.getUnits().add(u_degreeAngle);
		attr_type_position_longitude = attributetyperepository.save(attr_type_position_longitude);

		OCAttributeType attr_type_presenceState_emergencyVehicle = new OCAttributeType();
		attr_type_presenceState_emergencyVehicle.setName("presenceState:emergencyVehicle");
		attr_type_presenceState_emergencyVehicle.getUnits().add(u_index);
		attr_type_presenceState_emergencyVehicle = attributetyperepository.save(attr_type_presenceState_emergencyVehicle);

		OCAttributeType attr_type_presenceState_parking = new OCAttributeType();
		attr_type_presenceState_parking.setName("presenceState:parking");
		attr_type_presenceState_parking.getUnits().add(u_index);
		attr_type_presenceState_parking = attributetyperepository.save(attr_type_presenceState_parking);

		OCAttributeType attr_type_presenceState_people = new OCAttributeType();
		attr_type_presenceState_people.setName("presenceState:people");
		attr_type_presenceState_people.getUnits().add(u_index);
		attr_type_presenceState_people = attributetyperepository.save(attr_type_presenceState_people);

		OCAttributeType attr_type_pressure_atmospheric = new OCAttributeType();
		attr_type_pressure_atmospheric.setName("atmosphericPressure");
		attr_type_pressure_atmospheric.getUnits().add(u_bar);
		attr_type_pressure_atmospheric = attributetyperepository.save(attr_type_pressure_atmospheric);

		OCAttributeType attr_type_rainfall = new OCAttributeType();
		attr_type_rainfall.setName("rainfall");
		attr_type_rainfall.getUnits().add(u_millimetrePerHour);
		attr_type_rainfall = attributetyperepository.save(attr_type_rainfall);

		OCAttributeType attr_type_relativeHumidity = new OCAttributeType();
		attr_type_relativeHumidity.setName("relativeHumidity");
		attr_type_relativeHumidity.getUnits().add(u_percent);
		attr_type_relativeHumidity = attributetyperepository.save(attr_type_relativeHumidity);

		OCAttributeType attr_type_roadOccupancy = new OCAttributeType();
		attr_type_roadOccupancy.setName("roadOccupancy");
		attr_type_roadOccupancy.getUnits().add(u_percent);
		attr_type_roadOccupancy = attributetyperepository.save(attr_type_roadOccupancy);

		OCAttributeType attr_type_rotationalSpeed_engine = new OCAttributeType();
		attr_type_rotationalSpeed_engine.setName("rotationalSpeed:engine");
		attr_type_rotationalSpeed_engine.getUnits().add(u_revolutionPerMinute);
		attr_type_rotationalSpeed_engine = attributetyperepository.save(attr_type_rotationalSpeed_engine);

		OCAttributeType attr_type_soilMoistureTension = new OCAttributeType();
		attr_type_soilMoistureTension.setName("soilMoistureTension");
		attr_type_soilMoistureTension.getUnits().add(u_centibar);
		attr_type_soilMoistureTension = attributetyperepository.save(attr_type_soilMoistureTension);

		OCAttributeType attr_type_solarRadiation = new OCAttributeType();
		attr_type_solarRadiation.setName("solarRadiation");
		attr_type_solarRadiation.getUnits().add(u_wattPerSquareMetre);
		attr_type_solarRadiation = attributetyperepository.save(attr_type_solarRadiation);

		OCAttributeType attr_type_soundPressureLevel_ambient = new OCAttributeType();
		attr_type_soundPressureLevel_ambient.setName("soundPressureLevel:ambient");
		attr_type_soundPressureLevel_ambient.getUnits().add(u_decibel);
		attr_type_soundPressureLevel_ambient = attributetyperepository.save(attr_type_soundPressureLevel_ambient);

		OCAttributeType attr_type_speed = new OCAttributeType();
		attr_type_speed.setName("speed:average");
		attr_type_speed.getUnits().add(u_kilometrePerHour);
		attr_type_speed = attributetyperepository.save(attr_type_speed);

		OCAttributeType attr_type_temperature_ambient = new OCAttributeType();
		attr_type_temperature_ambient.setName("temperature:ambient");
		attr_type_temperature_ambient.getUnits().add(u_degreeCelsius);
		attr_type_temperature_ambient = attributetyperepository.save(attr_type_temperature_ambient);

		OCAttributeType attr_type_temperature_device = new OCAttributeType();
		attr_type_temperature_device.setName("temperature:device");
		attr_type_temperature_device.getUnits().add(u_degreeCelsius);
		attr_type_temperature_device = attributetyperepository.save(attr_type_temperature_device);

		OCAttributeType attr_type_temperature_engine = new OCAttributeType();
		attr_type_temperature_engine.setName("temperature:engine");
		attr_type_temperature_engine.getUnits().add(u_degreeCelsius);
		attr_type_temperature_engine = attributetyperepository.save(attr_type_temperature_engine);

		OCAttributeType attr_type_temperature_soil = new OCAttributeType();
		attr_type_temperature_soil.setName("temperature:soil");
		attr_type_temperature_soil.getUnits().add(u_degreeCelsius);
		attr_type_temperature_soil = attributetyperepository.save(attr_type_temperature_soil);

		OCAttributeType attr_type_temperature_wasteContainer = new OCAttributeType();
		attr_type_temperature_wasteContainer.setName("temperature:wasteContainer");
		attr_type_temperature_wasteContainer.getUnits().add(u_degreeCelsius);
		attr_type_temperature_wasteContainer = attributetyperepository.save(attr_type_temperature_wasteContainer);

		OCAttributeType attr_type_trafficCongestion = new OCAttributeType();
		attr_type_trafficCongestion.setName("trafficCongestion");
		attr_type_trafficCongestion.getUnits().add(u_index);
		attr_type_trafficCongestion = attributetyperepository.save(attr_type_trafficCongestion);

		OCAttributeType attr_type_trafficIntensity = new OCAttributeType();
		attr_type_trafficIntensity.setName("trafficIntensity");
		attr_type_trafficIntensity.getUnits().add(u_index);
		attr_type_trafficIntensity.getUnits().add(u_vehiclePerMinute);
		attr_type_trafficIntensity = attributetyperepository.save(attr_type_trafficIntensity);

		OCAttributeType attr_type_vehicleOverspeedState = new OCAttributeType();
		attr_type_vehicleOverspeedState.setName("vehicleOverspeedState");
		attr_type_vehicleOverspeedState.getUnits().add(u_index);
		attr_type_vehicleOverspeedState = attributetyperepository.save(attr_type_vehicleOverspeedState);

		OCAttributeType attr_type_windDirection = new OCAttributeType();
		attr_type_windDirection.setName("windDirection");
		attr_type_windDirection.getUnits().add(u_degreeAngle);
		attr_type_windDirection = attributetyperepository.save(attr_type_windDirection);

		OCAttributeType attr_type_windSpeed = new OCAttributeType();
		attr_type_windSpeed.setName("windSpeed");
		attr_type_windSpeed.getUnits().add(u_kilometrePerHour);
		attr_type_windSpeed = attributetyperepository.save(attr_type_windSpeed);
	}	
}
