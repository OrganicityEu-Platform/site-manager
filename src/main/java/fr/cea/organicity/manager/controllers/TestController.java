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
import fr.cea.organicity.manager.domain.OCUserInterest;
import fr.cea.organicity.manager.repositories.OCAppTypeRepository;
import fr.cea.organicity.manager.repositories.OCAssetTypeRepository;
import fr.cea.organicity.manager.repositories.OCAttributeTypeRepository;
import fr.cea.organicity.manager.repositories.OCDataTypeRepository;
import fr.cea.organicity.manager.repositories.OCServiceRepository;
import fr.cea.organicity.manager.repositories.OCSiteRepository;
import fr.cea.organicity.manager.repositories.OCToolRepository;
import fr.cea.organicity.manager.repositories.OCUnitRepository;
import fr.cea.organicity.manager.repositories.OCUserInterestRepository;

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
	@Autowired private OCAssetTypeRepository assettyperepository;
	@Autowired private OCUserInterestRepository userInterestRepository; 
	
	
	@RequestMapping
	public String test() {

		// SITES
		// =====

		OCSite site_aarhus = new OCSite();
		site_aarhus.setName("aarhus");
		site_aarhus.setEmail("contact@aarhus.dk");
		site_aarhus.setRelated("Aarhus is...");
		site_aarhus.setQuota(1000);
		site_aarhus.setRemQuota(1000);
		site_aarhus = siterepository.save(site_aarhus);

		OCService srv_aarhusservice1 = new OCService();
		srv_aarhusservice1.setName("aarhusservice1");
		srv_aarhusservice1.setSite(site_aarhus);
		srv_aarhusservice1 = servicerepository.save(srv_aarhusservice1);

		OCSite site_london = new OCSite();
		site_london.setName("london");
		site_london.setEmail("contact@london.uk");
		site_london.setRelated("london is...");
		site_london.setQuota(1000);
		site_london.setRemQuota(1000);
		site_london = siterepository.save(site_london);

		OCService srv_londonservice1 = new OCService();
		srv_londonservice1.setName("londonservice1");
		srv_londonservice1.setSite(site_london);
		srv_londonservice1 = servicerepository.save(srv_londonservice1);

		OCSite site_santander = new OCSite();
		site_santander.setName("santander");
		site_santander.setEmail("contact@santander.es");
		site_santander.setRelated("santander is...");
		site_santander.setQuota(1000);
		site_santander.setRemQuota(1000);
		site_santander = siterepository.save(site_santander);

		OCService srv_santanderservice1 = new OCService();
		srv_santanderservice1.setName("santanderservice1");
		srv_santanderservice1.setSite(site_santander);
		srv_santanderservice1 = servicerepository.save(srv_santanderservice1);

		OCSite site_provider = new OCSite();
		site_provider.setName("provider");
		site_provider.setEmail("contact@provider.dk");
		site_provider.setRelated("provider is...");
		site_provider.setQuota(1000);
		site_provider.setRemQuota(1000);
		site_provider = siterepository.save(site_provider);

		OCService srv_provider = new OCService();
		srv_provider.setName("provider");
		srv_provider.setSite(site_provider);
		srv_provider = servicerepository.save(srv_provider);

		OCSite site_experimenters = new OCSite();
		site_experimenters.setName("experimenters");
		site_experimenters.setEmail("contact@experimenters.dk");
		site_experimenters.setRelated("experimenters is...");
		site_experimenters.setQuota(1000);
		site_experimenters.setRemQuota(1000);
		site_experimenters = siterepository.save(site_experimenters);

		OCService srv_experimenter = new OCService();
		srv_experimenter.setName("experimenter");
		srv_experimenter.setSite(site_experimenters);
		srv_experimenter = servicerepository.save(srv_experimenter);


		// TOOLS
		// =====

		OCTool tool_EventBusAndDULRadio = new OCTool();
		tool_EventBusAndDULRadio.setName("EventBusAndDULRadio");
		tool_EventBusAndDULRadio.setUrl("https://organicityeu.github.io/EventBus/");
		tool_EventBusAndDULRadio.setDescription("eventbus is a nice tool");
		tool_EventBusAndDULRadio = toolrepository.save(tool_EventBusAndDULRadio);

		OCTool tool_SensiNact = new OCTool();
		tool_SensiNact.setName("SensiNact");
		tool_SensiNact.setUrl("https://organicityeu.github.io/tools/sensinact/");
		tool_SensiNact.setDescription("");
		tool_SensiNact = toolrepository.save(tool_SensiNact);

		OCTool tool_WebSocket = new OCTool();
		tool_WebSocket.setName("WebSocket");
		tool_WebSocket.setUrl("https://github.com/OrganicityEu/Processing-Websocket-Library");
		tool_WebSocket.setDescription("");
		tool_WebSocket = toolrepository.save(tool_WebSocket);

		OCTool tool_SmartphoneExperiment = new OCTool();
		tool_SmartphoneExperiment.setName("SmartphoneExperiment");
		tool_SmartphoneExperiment.setUrl("https://organicityeu.github.io/set.html");
		tool_SmartphoneExperiment.setDescription("");
		tool_SmartphoneExperiment = toolrepository.save(tool_SmartphoneExperiment);

		OCTool tool_TSmarT = new OCTool();
		tool_TSmarT.setName("TSmarT");
		tool_TSmarT.setUrl("https://organicityeu.github.io/tools/TSmarT/");
		tool_TSmarT.setDescription("");
		tool_TSmarT = toolrepository.save(tool_TSmarT);

		OCTool tool_TinkerSpace = new OCTool();
		tool_TinkerSpace.setName("TinkerSpace");
		tool_TinkerSpace.setUrl("https://organicityeu.github.io/TinkerSpace/");
		tool_TinkerSpace.setDescription("");
		tool_TinkerSpace = toolrepository.save(tool_TinkerSpace);


		// APP TYPE
		// ========

		OCAppType appType_desktop = new OCAppType();
		appType_desktop.setName("desktop");
		appType_desktop.setDescription("desktop application type");
		appType_desktop = apptyperepository.save(appType_desktop);

		OCAppType appType_mobile = new OCAppType();
		appType_mobile.setName("mobile");
		appType_mobile.setDescription("mobile application type");
		appType_mobile = apptyperepository.save(appType_mobile);

		OCAppType appType_web = new OCAppType();
		appType_web.setName("web");
		appType_web.setDescription("web application type");
		appType_web = apptyperepository.save(appType_web);

		OCAppType appType_smartphone = new OCAppType();
		appType_smartphone.setName("smartphone");
		appType_smartphone.setDescription("smartphone experiment type");
		appType_smartphone = apptyperepository.save(appType_smartphone);


		// DATA TYPE
		// =========

		OCDataType dataType_numeric = new OCDataType();
		dataType_numeric.setName("numeric");
		dataType_numeric = datatyperepository.save(dataType_numeric);

		OCDataType dataType_url = new OCDataType();
		dataType_url.setName("url");
		dataType_url = datatyperepository.save(dataType_url);


		// ASSET TYPE
		// ==========

		OCAssetType assetType_districtprofile = new OCAssetType();
		assetType_districtprofile.setName("districtprofile");
		assetType_districtprofile = assettyperepository.save(assetType_districtprofile);

		OCAssetType assetType_iotdevice = new OCAssetType();
		assetType_iotdevice.setName("iotdevice");
		assetType_iotdevice = assettyperepository.save(assetType_iotdevice);

		OCAssetType assetType_weatherstation = new OCAssetType();
		assetType_weatherstation.setName("weatherstation");
		assetType_weatherstation = assettyperepository.save(assetType_weatherstation);

		OCAssetType assetType_trafficstats = new OCAssetType();
		assetType_trafficstats.setName("trafficstats");
		assetType_trafficstats = assettyperepository.save(assetType_trafficstats);

		OCAssetType assetType_busstation = new OCAssetType();
		assetType_busstation.setName("busstation");
		assetType_busstation = assettyperepository.save(assetType_busstation);

		OCAssetType assetType_trainstation = new OCAssetType();
		assetType_trainstation.setName("trainstation");
		assetType_trainstation = assettyperepository.save(assetType_trainstation);

		OCAssetType assetType_tramstation = new OCAssetType();
		assetType_tramstation.setName("tramstation");
		assetType_tramstation = assettyperepository.save(assetType_tramstation);

		OCAssetType assetType_undergroundstation = new OCAssetType();
		assetType_undergroundstation.setName("undergroundstation");
		assetType_undergroundstation = assettyperepository.save(assetType_undergroundstation);

		OCAssetType assetType_environmentalstation = new OCAssetType();
		assetType_environmentalstation.setName("environmentalstation");
		assetType_environmentalstation = assettyperepository.save(assetType_environmentalstation);

		OCAssetType assetType_shelter = new OCAssetType();
		assetType_shelter.setName("shelter");
		assetType_shelter = assettyperepository.save(assetType_shelter);

		OCAssetType assetType_bikeStation = new OCAssetType();
		assetType_bikeStation.setName("bikeStation");
		assetType_bikeStation = assettyperepository.save(assetType_bikeStation);

		OCAssetType assetType_shop = new OCAssetType();
		assetType_shop.setName("shop");
		assetType_shop = assettyperepository.save(assetType_shop);

		OCAssetType assetType_busLine = new OCAssetType();
		assetType_busLine.setName("busLine");
		assetType_busLine = assettyperepository.save(assetType_busLine);

		OCAssetType assetType_taxiStation = new OCAssetType();
		assetType_taxiStation.setName("taxiStation");
		assetType_taxiStation = assettyperepository.save(assetType_taxiStation);

		OCAssetType assetType_district = new OCAssetType();
		assetType_district.setName("district");
		assetType_district = assettyperepository.save(assetType_district);

		OCAssetType assetType_section = new OCAssetType();
		assetType_section.setName("section");
		assetType_section = assettyperepository.save(assetType_section);

		OCAssetType assetType_event_culture = new OCAssetType();
		assetType_event_culture.setName("event:culture");
		assetType_event_culture = assettyperepository.save(assetType_event_culture);

		OCAssetType assetType_event_strike = new OCAssetType();
		assetType_event_strike.setName("event:strike");
		assetType_event_strike = assettyperepository.save(assetType_event_strike);

		OCAssetType assetType_iotdevice_luminosity = new OCAssetType();
		assetType_iotdevice_luminosity.setName("iotdevice:luminosity");
		assetType_iotdevice_luminosity = assettyperepository.save(assetType_iotdevice_luminosity);

		OCAssetType assetType_iotdevice_noise = new OCAssetType();
		assetType_iotdevice_noise.setName("iotdevice:noise");
		assetType_iotdevice_noise = assettyperepository.save(assetType_iotdevice_noise);

		OCAssetType assetType_iotdevice_vehiclecount = new OCAssetType();
		assetType_iotdevice_vehiclecount.setName("iotdevice:vehiclecount");
		assetType_iotdevice_vehiclecount = assettyperepository.save(assetType_iotdevice_vehiclecount);

		OCAssetType assetType_iotdevice_vehiclespeed = new OCAssetType();
		assetType_iotdevice_vehiclespeed.setName("iotdevice:vehiclespeed");
		assetType_iotdevice_vehiclespeed = assettyperepository.save(assetType_iotdevice_vehiclespeed);

		OCAssetType assetType_iotdevice_agriculture = new OCAssetType();
		assetType_iotdevice_agriculture.setName("iotdevice:agriculture");
		assetType_iotdevice_agriculture = assettyperepository.save(assetType_iotdevice_agriculture);

		OCAssetType assetType_iotdevice_irrigation = new OCAssetType();
		assetType_iotdevice_irrigation.setName("iotdevice:irrigation");
		assetType_iotdevice_irrigation = assettyperepository.save(assetType_iotdevice_irrigation);

		OCAssetType assetType_iotdevice_magneticLoop = new OCAssetType();
		assetType_iotdevice_magneticLoop.setName("iotdevice:magneticLoop");
		assetType_iotdevice_magneticLoop = assettyperepository.save(assetType_iotdevice_magneticLoop);

		OCAssetType assetType_trafficStatus = new OCAssetType();
		assetType_trafficStatus.setName("trafficStatus");
		assetType_trafficStatus = assettyperepository.save(assetType_trafficStatus);

		OCAssetType assetType_iotdevice_tsmart = new OCAssetType();
		assetType_iotdevice_tsmart.setName("iotdevice:tsmart");
		assetType_iotdevice_tsmart = assettyperepository.save(assetType_iotdevice_tsmart);

		OCAssetType assetType_speed_limit = new OCAssetType();
		assetType_speed_limit.setName("speed:limit");
		assetType_speed_limit = assettyperepository.save(assetType_speed_limit);

		OCAssetType assetType_pollutionsensor = new OCAssetType();
		assetType_pollutionsensor.setName("pollutionsensor");
		assetType_pollutionsensor = assettyperepository.save(assetType_pollutionsensor);

		OCAssetType assetType_busRoute = new OCAssetType();
		assetType_busRoute.setName("busRoute");
		assetType_busRoute = assettyperepository.save(assetType_busRoute);


		// UNIT
		// ====

		OCUnit u_bar = new OCUnit();
		u_bar.setName("bar");
		u_bar.setDatatype(dataType_numeric);
		u_bar = unitrepository.save(u_bar);

		OCUnit u_centibar = new OCUnit();
		u_centibar.setName("centibar");
		u_centibar.setDatatype(dataType_numeric);
		u_centibar = unitrepository.save(u_centibar);

		OCUnit u_decibel = new OCUnit();
		u_decibel.setName("decibel");
		u_decibel.setDatatype(dataType_numeric);
		u_decibel = unitrepository.save(u_decibel);

		OCUnit u_degreeAngle = new OCUnit();
		u_degreeAngle.setName("degreeAngle");
		u_degreeAngle.setDatatype(dataType_numeric);
		u_degreeAngle = unitrepository.save(u_degreeAngle);

		OCUnit u_degreeCelsius = new OCUnit();
		u_degreeCelsius.setName("degreeCelsius");
		u_degreeCelsius.setDatatype(dataType_numeric);
		u_degreeCelsius = unitrepository.save(u_degreeCelsius);

		OCUnit u_index = new OCUnit();
		u_index.setName("index");
		u_index.setDatatype(dataType_numeric);
		u_index = unitrepository.save(u_index);

		OCUnit u_kilogram = new OCUnit();
		u_kilogram.setName("kilogram");
		u_kilogram.setDatatype(dataType_numeric);
		u_kilogram = unitrepository.save(u_kilogram);

		OCUnit u_kilometrePerHour = new OCUnit();
		u_kilometrePerHour.setName("kilometrePerHour");
		u_kilometrePerHour.setDatatype(dataType_numeric);
		u_kilometrePerHour = unitrepository.save(u_kilometrePerHour);

		OCUnit u_kilometre = new OCUnit();
		u_kilometre.setName("kilometre");
		u_kilometre.setDatatype(dataType_numeric);
		u_kilometre = unitrepository.save(u_kilometre);

		OCUnit u_litrePer100Kilometres = new OCUnit();
		u_litrePer100Kilometres.setName("litrePer100Kilometres");
		u_litrePer100Kilometres.setDatatype(dataType_numeric);
		u_litrePer100Kilometres = unitrepository.save(u_litrePer100Kilometres);

		OCUnit u_litre = new OCUnit();
		u_litre.setName("litre");
		u_litre.setDatatype(dataType_numeric);
		u_litre = unitrepository.save(u_litre);

		OCUnit u_lumen = new OCUnit();
		u_lumen.setName("lumen");
		u_lumen.setDatatype(dataType_numeric);
		u_lumen = unitrepository.save(u_lumen);

		OCUnit u_lux = new OCUnit();
		u_lux.setName("lux");
		u_lux.setDatatype(dataType_numeric);
		u_lux = unitrepository.save(u_lux);

		OCUnit u_metre = new OCUnit();
		u_metre.setName("metre");
		u_metre.setDatatype(dataType_numeric);
		u_metre = unitrepository.save(u_metre);

		OCUnit u_microgramPerCubicMetre = new OCUnit();
		u_microgramPerCubicMetre.setName("microgramPerCubicMetre");
		u_microgramPerCubicMetre.setDatatype(dataType_numeric);
		u_microgramPerCubicMetre = unitrepository.save(u_microgramPerCubicMetre);

		OCUnit u_milligramPerCubicMetre = new OCUnit();
		u_milligramPerCubicMetre.setName("milligramPerCubicMetre");
		u_milligramPerCubicMetre.setDatatype(dataType_numeric);
		u_milligramPerCubicMetre = unitrepository.save(u_milligramPerCubicMetre);

		OCUnit u_millimetrePerHour = new OCUnit();
		u_millimetrePerHour.setName("millimetrePerHour");
		u_millimetrePerHour.setDatatype(dataType_numeric);
		u_millimetrePerHour = unitrepository.save(u_millimetrePerHour);

		OCUnit u_millivoltPerMetre = new OCUnit();
		u_millivoltPerMetre.setName("millivoltPerMetre");
		u_millivoltPerMetre.setDatatype(dataType_numeric);
		u_millivoltPerMetre = unitrepository.save(u_millivoltPerMetre);

		OCUnit u_percent = new OCUnit();
		u_percent.setName("percent");
		u_percent.setDatatype(dataType_numeric);
		u_percent = unitrepository.save(u_percent);

		OCUnit u_revolutionPerMinute = new OCUnit();
		u_revolutionPerMinute.setName("revolutionPerMinute");
		u_revolutionPerMinute.setDatatype(dataType_numeric);
		u_revolutionPerMinute = unitrepository.save(u_revolutionPerMinute);

		OCUnit u_vehiclePerMinute = new OCUnit();
		u_vehiclePerMinute.setName("vehiclePerMinute");
		u_vehiclePerMinute.setDatatype(dataType_numeric);
		u_vehiclePerMinute = unitrepository.save(u_vehiclePerMinute);

		OCUnit u_wattPerSquareMetre = new OCUnit();
		u_wattPerSquareMetre.setName("wattPerSquareMetre");
		u_wattPerSquareMetre.setDatatype(dataType_numeric);
		u_wattPerSquareMetre = unitrepository.save(u_wattPerSquareMetre);


		// ATTRIBUTE TYPE
		// ==============

		OCAttributeType attrType_batteryLevel = new OCAttributeType();
		attrType_batteryLevel.setName("batteryLevel");
		attrType_batteryLevel.getUnits().add(u_percent);
		attrType_batteryLevel = attributetyperepository.save(attrType_batteryLevel);

		OCAttributeType attrType_chemicalAgentAtmosphericConcentration_airParticles = new OCAttributeType();
		attrType_chemicalAgentAtmosphericConcentration_airParticles.setName("chemicalAgentAtmosphericConcentration:airParticles");
		attrType_chemicalAgentAtmosphericConcentration_airParticles.getUnits().add(u_milligramPerCubicMetre);
		attrType_chemicalAgentAtmosphericConcentration_airParticles = attributetyperepository.save(attrType_chemicalAgentAtmosphericConcentration_airParticles);

		OCAttributeType attrType_chemicalAgentAtmosphericConcentration_CO = new OCAttributeType();
		attrType_chemicalAgentAtmosphericConcentration_CO.setName("chemicalAgentAtmosphericConcentration:CO");
		attrType_chemicalAgentAtmosphericConcentration_CO.getUnits().add(u_milligramPerCubicMetre);
		attrType_chemicalAgentAtmosphericConcentration_CO = attributetyperepository.save(attrType_chemicalAgentAtmosphericConcentration_CO);

		OCAttributeType attrType_chemicalAgentAtmosphericConcentration_NO2 = new OCAttributeType();
		attrType_chemicalAgentAtmosphericConcentration_NO2.setName("chemicalAgentAtmosphericConcentration:NO2");
		attrType_chemicalAgentAtmosphericConcentration_NO2.getUnits().add(u_microgramPerCubicMetre);
		attrType_chemicalAgentAtmosphericConcentration_NO2 = attributetyperepository.save(attrType_chemicalAgentAtmosphericConcentration_NO2);

		OCAttributeType attrType_chemicalAgentAtmosphericConcentration_O3 = new OCAttributeType();
		attrType_chemicalAgentAtmosphericConcentration_O3.setName("chemicalAgentAtmosphericConcentration:O3");
		attrType_chemicalAgentAtmosphericConcentration_O3.getUnits().add(u_microgramPerCubicMetre);
		attrType_chemicalAgentAtmosphericConcentration_O3 = attributetyperepository.save(attrType_chemicalAgentAtmosphericConcentration_O3);

		OCAttributeType attrType_chemicalAgentAtmosphericConcentration_SO2 = new OCAttributeType();
		attrType_chemicalAgentAtmosphericConcentration_SO2.setName("chemicalAgentAtmosphericConcentration:SO2");
		attrType_chemicalAgentAtmosphericConcentration_SO2.getUnits().add(u_microgramPerCubicMetre);
		attrType_chemicalAgentAtmosphericConcentration_SO2 = attributetyperepository.save(attrType_chemicalAgentAtmosphericConcentration_SO2);

		OCAttributeType attrType_direction_azimuth = new OCAttributeType();
		attrType_direction_azimuth.setName("direction:azimuth");
		attrType_direction_azimuth.getUnits().add(u_degreeAngle);
		attrType_direction_azimuth = attributetyperepository.save(attrType_direction_azimuth);

		OCAttributeType attrType_direction_heading = new OCAttributeType();
		attrType_direction_heading.setName("direction:heading");
		attrType_direction_heading.getUnits().add(u_index);
		attrType_direction_heading = attributetyperepository.save(attrType_direction_heading);

		OCAttributeType attrType_electricField_1800mhz = new OCAttributeType();
		attrType_electricField_1800mhz.setName("electricField:1800mhz");
		attrType_electricField_1800mhz.getUnits().add(u_millivoltPerMetre);
		attrType_electricField_1800mhz = attributetyperepository.save(attrType_electricField_1800mhz);

		OCAttributeType attrType_electricField_2100mhz = new OCAttributeType();
		attrType_electricField_2100mhz.setName("electricField:2100mhz");
		attrType_electricField_2100mhz.getUnits().add(u_millivoltPerMetre);
		attrType_electricField_2100mhz = attributetyperepository.save(attrType_electricField_2100mhz);

		OCAttributeType attrType_electricField_2400mhz = new OCAttributeType();
		attrType_electricField_2400mhz.setName("electricField:2400mhz");
		attrType_electricField_2400mhz.getUnits().add(u_millivoltPerMetre);
		attrType_electricField_2400mhz = attributetyperepository.save(attrType_electricField_2400mhz);

		OCAttributeType attrType_electricField_900mhz = new OCAttributeType();
		attrType_electricField_900mhz.setName("electricField:900mhz");
		attrType_electricField_900mhz.getUnits().add(u_millivoltPerMetre);
		attrType_electricField_900mhz = attributetyperepository.save(attrType_electricField_900mhz);

		OCAttributeType attrType_fillLevel_wasteContainer = new OCAttributeType();
		attrType_fillLevel_wasteContainer.setName("fillLevel:wasteContainer");
		attrType_fillLevel_wasteContainer.getUnits().add(u_percent);
		attrType_fillLevel_wasteContainer = attributetyperepository.save(attrType_fillLevel_wasteContainer);

		OCAttributeType attrType_fuelConsumption_instantaneous = new OCAttributeType();
		attrType_fuelConsumption_instantaneous.setName("fuelConsumption:instantaneous");
		attrType_fuelConsumption_instantaneous.getUnits().add(u_litrePer100Kilometres);
		attrType_fuelConsumption_instantaneous = attributetyperepository.save(attrType_fuelConsumption_instantaneous);

		OCAttributeType attrType_fuelConsumption_total = new OCAttributeType();
		attrType_fuelConsumption_total.setName("fuelConsumption:total");
		attrType_fuelConsumption_total.getUnits().add(u_litre);
		attrType_fuelConsumption_total = attributetyperepository.save(attrType_fuelConsumption_total);

		OCAttributeType attrType_illuminance = new OCAttributeType();
		attrType_illuminance.setName("illuminance");
		attrType_illuminance.getUnits().add(u_lux);
		attrType_illuminance = attributetyperepository.save(attrType_illuminance);

		OCAttributeType attrType_luminosity = new OCAttributeType();
		attrType_luminosity.setName("luminosity");
		attrType_luminosity.getUnits().add(u_lumen);
		attrType_luminosity = attributetyperepository.save(attrType_luminosity);

		OCAttributeType attrType_mass = new OCAttributeType();
		attrType_mass.setName("mass");
		attrType_mass.getUnits().add(u_kilogram);
		attrType_mass = attributetyperepository.save(attrType_mass);

		OCAttributeType attrType_mileage_distanceToService = new OCAttributeType();
		attrType_mileage_distanceToService.setName("mileage:distanceToService");
		attrType_mileage_distanceToService.getUnits().add(u_kilometre);
		attrType_mileage_distanceToService = attributetyperepository.save(attrType_mileage_distanceToService);

		OCAttributeType attrType_mileage_total = new OCAttributeType();
		attrType_mileage_total.setName("mileage:total");
		attrType_mileage_total.getUnits().add(u_metre);
		attrType_mileage_total = attributetyperepository.save(attrType_mileage_total);

		OCAttributeType attrType_motionState_vehicle = new OCAttributeType();
		attrType_motionState_vehicle.setName("motionState:vehicle");
		attrType_motionState_vehicle.getUnits().add(u_index);
		attrType_motionState_vehicle = attributetyperepository.save(attrType_motionState_vehicle);

		OCAttributeType attrType_odometer = new OCAttributeType();
		attrType_odometer.setName("odometer");
		attrType_odometer.getUnits().add(u_kilometre);
		attrType_odometer = attributetyperepository.save(attrType_odometer);

		OCAttributeType attrType_position_altitude = new OCAttributeType();
		attrType_position_altitude.setName("position:altitude");
		attrType_position_altitude.getUnits().add(u_metre);
		attrType_position_altitude = attributetyperepository.save(attrType_position_altitude);

		OCAttributeType attrType_position_latitude = new OCAttributeType();
		attrType_position_latitude.setName("position:latitude");
		attrType_position_latitude.getUnits().add(u_degreeAngle);
		attrType_position_latitude = attributetyperepository.save(attrType_position_latitude);

		OCAttributeType attrType_position_longitude = new OCAttributeType();
		attrType_position_longitude.setName("position:longitude");
		attrType_position_longitude.getUnits().add(u_degreeAngle);
		attrType_position_longitude = attributetyperepository.save(attrType_position_longitude);

		OCAttributeType attrType_presenceState_emergencyVehicle = new OCAttributeType();
		attrType_presenceState_emergencyVehicle.setName("presenceState:emergencyVehicle");
		attrType_presenceState_emergencyVehicle.getUnits().add(u_index);
		attrType_presenceState_emergencyVehicle = attributetyperepository.save(attrType_presenceState_emergencyVehicle);

		OCAttributeType attrType_presenceState_parking = new OCAttributeType();
		attrType_presenceState_parking.setName("presenceState:parking");
		attrType_presenceState_parking.getUnits().add(u_index);
		attrType_presenceState_parking = attributetyperepository.save(attrType_presenceState_parking);

		OCAttributeType attrType_presenceState_people = new OCAttributeType();
		attrType_presenceState_people.setName("presenceState:people");
		attrType_presenceState_people.getUnits().add(u_index);
		attrType_presenceState_people = attributetyperepository.save(attrType_presenceState_people);

		OCAttributeType attrType_atmosphericPressure = new OCAttributeType();
		attrType_atmosphericPressure.setName("atmosphericPressure");
		attrType_atmosphericPressure.getUnits().add(u_bar);
		attrType_atmosphericPressure = attributetyperepository.save(attrType_atmosphericPressure);

		OCAttributeType attrType_rainfall = new OCAttributeType();
		attrType_rainfall.setName("rainfall");
		attrType_rainfall.getUnits().add(u_millimetrePerHour);
		attrType_rainfall = attributetyperepository.save(attrType_rainfall);

		OCAttributeType attrType_relativeHumidity = new OCAttributeType();
		attrType_relativeHumidity.setName("relativeHumidity");
		attrType_relativeHumidity.getUnits().add(u_percent);
		attrType_relativeHumidity = attributetyperepository.save(attrType_relativeHumidity);

		OCAttributeType attrType_roadOccupancy = new OCAttributeType();
		attrType_roadOccupancy.setName("roadOccupancy");
		attrType_roadOccupancy.getUnits().add(u_percent);
		attrType_roadOccupancy = attributetyperepository.save(attrType_roadOccupancy);

		OCAttributeType attrType_rotationalSpeed_engine = new OCAttributeType();
		attrType_rotationalSpeed_engine.setName("rotationalSpeed:engine");
		attrType_rotationalSpeed_engine.getUnits().add(u_revolutionPerMinute);
		attrType_rotationalSpeed_engine = attributetyperepository.save(attrType_rotationalSpeed_engine);

		OCAttributeType attrType_soilMoistureTension = new OCAttributeType();
		attrType_soilMoistureTension.setName("soilMoistureTension");
		attrType_soilMoistureTension.getUnits().add(u_centibar);
		attrType_soilMoistureTension = attributetyperepository.save(attrType_soilMoistureTension);

		OCAttributeType attrType_solarRadiation = new OCAttributeType();
		attrType_solarRadiation.setName("solarRadiation");
		attrType_solarRadiation.getUnits().add(u_wattPerSquareMetre);
		attrType_solarRadiation = attributetyperepository.save(attrType_solarRadiation);

		OCAttributeType attrType_soundPressureLevel_ambient = new OCAttributeType();
		attrType_soundPressureLevel_ambient.setName("soundPressureLevel:ambient");
		attrType_soundPressureLevel_ambient.getUnits().add(u_decibel);
		attrType_soundPressureLevel_ambient = attributetyperepository.save(attrType_soundPressureLevel_ambient);

		OCAttributeType attrType_speed_average = new OCAttributeType();
		attrType_speed_average.setName("speed:average");
		attrType_speed_average.getUnits().add(u_kilometrePerHour);
		attrType_speed_average = attributetyperepository.save(attrType_speed_average);

		OCAttributeType attrType_temperature_ambient = new OCAttributeType();
		attrType_temperature_ambient.setName("temperature:ambient");
		attrType_temperature_ambient.getUnits().add(u_degreeCelsius);
		attrType_temperature_ambient = attributetyperepository.save(attrType_temperature_ambient);

		OCAttributeType attrType_temperature_device = new OCAttributeType();
		attrType_temperature_device.setName("temperature:device");
		attrType_temperature_device.getUnits().add(u_degreeCelsius);
		attrType_temperature_device = attributetyperepository.save(attrType_temperature_device);

		OCAttributeType attrType_temperature_engine = new OCAttributeType();
		attrType_temperature_engine.setName("temperature:engine");
		attrType_temperature_engine.getUnits().add(u_degreeCelsius);
		attrType_temperature_engine = attributetyperepository.save(attrType_temperature_engine);

		OCAttributeType attrType_temperature_soil = new OCAttributeType();
		attrType_temperature_soil.setName("temperature:soil");
		attrType_temperature_soil.getUnits().add(u_degreeCelsius);
		attrType_temperature_soil = attributetyperepository.save(attrType_temperature_soil);

		OCAttributeType attrType_temperature_wasteContainer = new OCAttributeType();
		attrType_temperature_wasteContainer.setName("temperature:wasteContainer");
		attrType_temperature_wasteContainer.getUnits().add(u_degreeCelsius);
		attrType_temperature_wasteContainer = attributetyperepository.save(attrType_temperature_wasteContainer);

		OCAttributeType attrType_trafficCongestion = new OCAttributeType();
		attrType_trafficCongestion.setName("trafficCongestion");
		attrType_trafficCongestion.getUnits().add(u_index);
		attrType_trafficCongestion = attributetyperepository.save(attrType_trafficCongestion);

		OCAttributeType attrType_trafficIntensity = new OCAttributeType();
		attrType_trafficIntensity.setName("trafficIntensity");
		attrType_trafficIntensity.getUnits().add(u_index);
		attrType_trafficIntensity.getUnits().add(u_vehiclePerMinute);
		attrType_trafficIntensity = attributetyperepository.save(attrType_trafficIntensity);

		OCAttributeType attrType_vehicleOverspeedState = new OCAttributeType();
		attrType_vehicleOverspeedState.setName("vehicleOverspeedState");
		attrType_vehicleOverspeedState.getUnits().add(u_index);
		attrType_vehicleOverspeedState = attributetyperepository.save(attrType_vehicleOverspeedState);

		OCAttributeType attrType_windDirection = new OCAttributeType();
		attrType_windDirection.setName("windDirection");
		attrType_windDirection.getUnits().add(u_degreeAngle);
		attrType_windDirection = attributetyperepository.save(attrType_windDirection);

		OCAttributeType attrType_windSpeed = new OCAttributeType();
		attrType_windSpeed.setName("windSpeed");
		attrType_windSpeed.getUnits().add(u_kilometrePerHour);
		attrType_windSpeed = attributetyperepository.save(attrType_windSpeed);

		OCAttributeType attrType_temperature = new OCAttributeType();
		attrType_temperature.setName("temperature");
		attrType_temperature.getUnits().add(u_degreeCelsius);
		attrType_temperature = attributetyperepository.save(attrType_temperature);

		OCAttributeType attrType_humidity = new OCAttributeType();
		attrType_humidity.setName("humidity");
		attrType_humidity.getUnits().add(u_percent);
		attrType_humidity = attributetyperepository.save(attrType_humidity);

		OCAttributeType attrType_location = new OCAttributeType();
		attrType_location.setName("location");
		attrType_location = attributetyperepository.save(attrType_location);

		OCAttributeType attrType_speed_limit = new OCAttributeType();
		attrType_speed_limit.setName("speed:limit");
		attrType_speed_limit.getUnits().add(u_kilometrePerHour);
		attrType_speed_limit = attributetyperepository.save(attrType_speed_limit);


		// USER INTEREST
		// =============

		OCUserInterest userInterest_traffic = new OCUserInterest();
		userInterest_traffic.setName("traffic");
		userInterest_traffic.setDescription("");
		userInterest_traffic = userInterestRepository.save(userInterest_traffic);

		OCUserInterest userInterest_mobility = new OCUserInterest();
		userInterest_mobility.setName("mobility");
		userInterest_mobility.setDescription("");
		userInterest_mobility = userInterestRepository.save(userInterest_mobility);

		OCUserInterest userInterest_environmental = new OCUserInterest();
		userInterest_environmental.setName("environmental");
		userInterest_environmental.setDescription("");
		userInterest_environmental = userInterestRepository.save(userInterest_environmental);

		OCUserInterest userInterest_parking = new OCUserInterest();
		userInterest_parking.setName("parking");
		userInterest_parking.setDescription("");
		userInterest_parking = userInterestRepository.save(userInterest_parking);

		OCUserInterest userInterest_park = new OCUserInterest();
		userInterest_park.setName("park");
		userInterest_park.setDescription("");
		userInterest_park = userInterestRepository.save(userInterest_park);

		OCUserInterest userInterest_garden = new OCUserInterest();
		userInterest_garden.setName("garden");
		userInterest_garden.setDescription("");
		userInterest_garden = userInterestRepository.save(userInterest_garden);

		OCUserInterest userInterest_beach = new OCUserInterest();
		userInterest_beach.setName("beach");
		userInterest_beach.setDescription("");
		userInterest_beach = userInterestRepository.save(userInterest_beach);

		OCUserInterest userInterest_airquality = new OCUserInterest();
		userInterest_airquality.setName("airquality");
		userInterest_airquality.setDescription("");
		userInterest_airquality = userInterestRepository.save(userInterest_airquality);

		OCUserInterest userInterest_tubestations = new OCUserInterest();
		userInterest_tubestations.setName("tubestations");
		userInterest_tubestations.setDescription("");
		userInterest_tubestations = userInterestRepository.save(userInterest_tubestations);

		OCUserInterest userInterest_busstations = new OCUserInterest();
		userInterest_busstations.setName("busstations");
		userInterest_busstations.setDescription("");
		userInterest_busstations = userInterestRepository.save(userInterest_busstations);

		OCUserInterest userInterest_bikestations = new OCUserInterest();
		userInterest_bikestations.setName("bikestations");
		userInterest_bikestations.setDescription("");
		userInterest_bikestations = userInterestRepository.save(userInterest_bikestations);

		OCUserInterest userInterest_tubelines = new OCUserInterest();
		userInterest_tubelines.setName("tubelines");
		userInterest_tubelines.setDescription("");
		userInterest_tubelines = userInterestRepository.save(userInterest_tubelines);

		OCUserInterest userInterest_buslines = new OCUserInterest();
		userInterest_buslines.setName("buslines");
		userInterest_buslines.setDescription("");
		userInterest_buslines = userInterestRepository.save(userInterest_buslines);

		OCUserInterest userInterest_neighbourhood = new OCUserInterest();
		userInterest_neighbourhood.setName("neighbourhood");
		userInterest_neighbourhood.setDescription("");
		userInterest_neighbourhood = userInterestRepository.save(userInterest_neighbourhood);

		OCUserInterest userInterest_cultural = new OCUserInterest();
		userInterest_cultural.setName("cultural");
		userInterest_cultural.setDescription("");
		userInterest_cultural = userInterestRepository.save(userInterest_cultural);

		OCUserInterest userInterest_cityevent = new OCUserInterest();
		userInterest_cityevent.setName("cityevent");
		userInterest_cityevent.setDescription("");
		userInterest_cityevent = userInterestRepository.save(userInterest_cityevent);

		OCUserInterest userInterest_shopping = new OCUserInterest();
		userInterest_shopping.setName("shopping");
		userInterest_shopping.setDescription("");
		userInterest_shopping = userInterestRepository.save(userInterest_shopping);

		OCUserInterest userInterest_bikelane = new OCUserInterest();
		userInterest_bikelane.setName("bikelane");
		userInterest_bikelane.setDescription("");
		userInterest_bikelane = userInterestRepository.save(userInterest_bikelane);

		OCUserInterest userInterest_bikestop = new OCUserInterest();
		userInterest_bikestop.setName("bikestop");
		userInterest_bikestop.setDescription("");
		userInterest_bikestop = userInterestRepository.save(userInterest_bikestop);

		OCUserInterest userInterest_taxistop = new OCUserInterest();
		userInterest_taxistop.setName("taxistop");
		userInterest_taxistop.setDescription("");
		userInterest_taxistop = userInterestRepository.save(userInterest_taxistop);

		return "{message: \"Data added to database\"}";
	}
}
