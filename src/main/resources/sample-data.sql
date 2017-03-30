INSERT INTO PUBLIC.OCUSER_INTEREST(URN, DESCRIPTION, NAME) VALUES

('urn:oc:userInterest:traffic', '', 'traffic'),
('urn:oc:userInterest:mobility', '', 'mobility'),
('urn:oc:userInterest:environmental', '', 'environmental'),
('urn:oc:userInterest:parking', '', 'parking'),
('urn:oc:userInterest:park', '', 'park'),
('urn:oc:userInterest:garden', '', 'garden'),
('urn:oc:userInterest:beach', '', 'beach'),
('urn:oc:userInterest:airquality', '', 'airquality'),
('urn:oc:userInterest:tubestations', '', 'tubestations'),
('urn:oc:userInterest:busstations', '', 'busstations'),
('urn:oc:userInterest:bikestations', '', 'bikestations'),
('urn:oc:userInterest:tubelines', '', 'tubelines'),
('urn:oc:userInterest:buslines', '', 'buslines'),
('urn:oc:userInterest:neighbourhood', '', 'neighbourhood'),
('urn:oc:userInterest:cultural', '', 'cultural'),
('urn:oc:userInterest:cityevent', '', 'cityevent'),
('urn:oc:userInterest:shopping', '', 'shopping'),
('urn:oc:userInterest:bikelane', '', 'bikelane'),
('urn:oc:userInterest:bikestop', '', 'bikestop'),
('urn:oc:userInterest:taxistop', '', 'taxistop');


--INSERT INTO PUBLIC.OCUNREGISTERED_ASSET_TYPE(URN, NAME) VALUES


INSERT INTO PUBLIC.OCDATA_TYPE(URN, NAME) VALUES

('urn:oc:datatype:numeric', 'numeric'),
('urn:oc:datatype:url', 'url');


INSERT INTO PUBLIC.OCUNIT(URN, DESCRIPTION, NAME, RELATED, DATATYPE_URN) VALUES

('urn:oc:uom:bar', '', 'bar', '', 'urn:oc:datatype:numeric'),
('urn:oc:uom:centibar', '', 'centibar', '', 'urn:oc:datatype:numeric'),
('urn:oc:uom:decibel', '', 'decibel', '', 'urn:oc:datatype:numeric'),
('urn:oc:uom:degreeAngle', '', 'degreeAngle', '', 'urn:oc:datatype:numeric'),
('urn:oc:uom:degreeCelsius', '', 'degreeCelsius', '', 'urn:oc:datatype:numeric'),
('urn:oc:uom:index', '', 'index', '', 'urn:oc:datatype:numeric'),
('urn:oc:uom:kilogram', '', 'kilogram', '', 'urn:oc:datatype:numeric'),
('urn:oc:uom:kilometrePerHour', '', 'kilometrePerHour', '', 'urn:oc:datatype:numeric'),
('urn:oc:uom:kilometre', '', 'kilometre', '', 'urn:oc:datatype:numeric'),
('urn:oc:uom:litrePer100Kilometres', '', 'litrePer100Kilometres', '', 'urn:oc:datatype:numeric'),
('urn:oc:uom:litre', '', 'litre', '', 'urn:oc:datatype:numeric'),
('urn:oc:uom:lumen', '', 'lumen', '', 'urn:oc:datatype:numeric'),
('urn:oc:uom:lux', '', 'lux', '', 'urn:oc:datatype:numeric'),
('urn:oc:uom:metre', '', 'metre', '', 'urn:oc:datatype:numeric'),
('urn:oc:uom:microgramPerCubicMetre', '', 'microgramPerCubicMetre', '', 'urn:oc:datatype:numeric'),
('urn:oc:uom:milligramPerCubicMetre', '', 'milligramPerCubicMetre', '', 'urn:oc:datatype:numeric'),
('urn:oc:uom:millimetrePerHour', '', 'millimetrePerHour', '', 'urn:oc:datatype:numeric'),
('urn:oc:uom:millivoltPerMetre', '', 'millivoltPerMetre', '', 'urn:oc:datatype:numeric'),
('urn:oc:uom:percent', '', 'percent', '', 'urn:oc:datatype:numeric'),
('urn:oc:uom:revolutionPerMinute', '', 'revolutionPerMinute', '', 'urn:oc:datatype:numeric'),
('urn:oc:uom:vehiclePerMinute', '', 'vehiclePerMinute', '', 'urn:oc:datatype:numeric'),
('urn:oc:uom:wattPerSquareMetre', '', 'wattPerSquareMetre', '', 'urn:oc:datatype:numeric');


INSERT INTO PUBLIC.OCTOOL(URN, DESCRIPTION, NAME, URL) VALUES

('urn:oc:tool:EventBusAndDULRadio', 'eventbus is a nice tool', 'EventBusAndDULRadio', 'https://organicityeu.github.io/EventBus/'),
('urn:oc:tool:SensiNact', '', 'SensiNact', 'https://organicityeu.github.io/tools/sensinact/'),
('urn:oc:tool:WebSocket', '', 'WebSocket', 'https://github.com/OrganicityEu/Processing-Websocket-Library'),
('urn:oc:tool:SmartphoneExperiment', '', 'SmartphoneExperiment', 'https://organicityeu.github.io/set.html'),
('urn:oc:tool:TSmarT', '', 'TSmarT', 'https://organicityeu.github.io/tools/TSmarT/'),
('urn:oc:tool:TinkerSpace', '', 'TinkerSpace', 'https://organicityeu.github.io/TinkerSpace/'),
('urn:oc:tool:oppnet', 'opportunistic network', 'oppnet', 'http://organicity.eu/tools/opportunistic-connectivity-services/');


INSERT INTO PUBLIC.OCSITE(URN, NAME, EMAIL, RELATED, LATITUDE, LONGITUDE, CITY, REGION, COUNTRY_CODE, COUNTRY, WIKI, QUOTA, REM_QUOTA, CREATED, UPDATED) VALUES

('urn:oc:site:santander', 'santander', 'contact@santander.es', 'The port city of Santander is the capital of the autonomous community and historical region of Cantabria situated on the north coast of Spain. Located east of Gijón and west of Bilbao, the city has a population of 178,465.', '43.45487', '-3.81289', 'Santander', 'Cantabria', 'ES', 'Spain', 'https://en.wikipedia.org/wiki/Santander,_Spain', 1000, 1000, '2017-01-20T11:20:28.372Z', '2017-01-20T11:20:28.372Z'),
('urn:oc:site:london', 'london', 'contact@london.uk', 'London is the capital and most populous city of England and the United Kingdom.', '51.507222', '-0.1275', 'London', 'England', 'UK', 'United Kingdom', 'https://en.wikipedia.org/wiki/London', 1000, 1000, '2017-01-20T11:20:28.366Z', '2017-01-20T11:20:28.366Z'),
('urn:oc:site:aarhus', 'aarhus', 'contact@aarhus.dk', 'Aarhus is the second-largest city in Denmark and the seat of Aarhus Municipality.', '56.1572', '10.2107', 'Aarhus', 'East Jutland', 'DK', 'Denmark', 'https://en.wikipedia.org/wiki/Aarhus', 1000, 1000, '2017-01-20T11:20:28.322Z', '2017-01-20T11:20:28.322Z'),
('urn:oc:site:patras', 'patras', 'amaxilat@cti.gr', 'Patras (or Patra) is the third largest city in Greece and the regional capital of Western Greece, in northern Peloponnese, 215 km (134 mi) west of Athens. The city is built at the foothills of Mount Panachaikon, overlooking the Gulf of Patras.', '38.25', '21.733333', 'Patras', 'Western Greece', 'GR', 'Greece', 'https://en.wikipedia.org/wiki/Patras', 1000, 1000, '2017-03-15T08:59:05.539Z', '2017-03-15T08:59:05.539Z'),
('urn:oc:site:provider', 'provider', 'contact@provider.com', '....', '', '', '', '', '', '', '', 1000, 1000, '2017-03-15T08:59:05.539Z', '2017-03-15T08:59:05.539Z'),
('urn:oc:site:experimenters', 'experimenters', 'contact@experimenters.com', '....', '', '', '', '', '', '', '', 1000, 1000, '2017-03-15T08:59:05.539Z', '2017-03-15T08:59:05.539Z');


INSERT INTO PUBLIC.OCSERVICE(URN, CREATED, DESCRIPTION, NAME, RELATED, UPDATED, SITE_URN) VALUES

('urn:oc:site:aarhus:aarhusservice1', '2017-01-20T11:20:28.363Z', '', 'aarhusservice1', '', '2017-01-20T11:20:28.363Z', 'urn:oc:site:aarhus'),
('urn:oc:site:london:londonservice1', '2017-01-20T11:20:28.370Z', '', 'londonservice1', '', '2017-01-20T11:20:28.370Z', 'urn:oc:site:london'),
('urn:oc:site:santander:santanderservice1', '2017-01-20T11:20:28.374Z', '', 'santanderservice1', '', '2017-01-20T11:20:28.374Z', 'urn:oc:site:santander'),
('urn:oc:site:provider:provider', '2017-01-20T11:20:28.378Z', '', 'provider', '', '2017-01-20T11:20:28.378Z', 'urn:oc:site:provider'),
('urn:oc:site:experimenters:experimenter', '2017-01-20T11:20:28.383Z', '', 'experimenter', '', '2017-01-20T11:20:28.383Z', 'urn:oc:site:experimenters');


INSERT INTO PUBLIC.OCATTRIBUTE_TYPE(URN, DESCRIPTION, NAME, RELATED) VALUES

('urn:oc:attributetype:batteryLevel', '', 'batteryLevel', ''),
('urn:oc:attributetype:chemicalAgentAtmosphericConcentration:airParticles', '', 'chemicalAgentAtmosphericConcentration:airParticles', ''),
('urn:oc:attributetype:chemicalAgentAtmosphericConcentration:CO', '', 'chemicalAgentAtmosphericConcentration:CO', ''),
('urn:oc:attributetype:chemicalAgentAtmosphericConcentration:NO2', '', 'chemicalAgentAtmosphericConcentration:NO2', ''),
('urn:oc:attributetype:chemicalAgentAtmosphericConcentration:O3', '', 'chemicalAgentAtmosphericConcentration:O3', ''),
('urn:oc:attributetype:chemicalAgentAtmosphericConcentration:SO2', '', 'chemicalAgentAtmosphericConcentration:SO2', ''),
('urn:oc:attributetype:direction:azimuth', '', 'direction:azimuth', ''),
('urn:oc:attributetype:direction:heading', '', 'direction:heading', ''),
('urn:oc:attributetype:electricField:1800mhz', '', 'electricField:1800mhz', ''),
('urn:oc:attributetype:electricField:2100mhz', '', 'electricField:2100mhz', ''),
('urn:oc:attributetype:electricField:2400mhz', '', 'electricField:2400mhz', ''),
('urn:oc:attributetype:electricField:900mhz', '', 'electricField:900mhz', ''),
('urn:oc:attributetype:fillLevel:wasteContainer', '', 'fillLevel:wasteContainer', ''),
('urn:oc:attributetype:fuelConsumption:instantaneous', '', 'fuelConsumption:instantaneous', ''),
('urn:oc:attributetype:fuelConsumption:total', '', 'fuelConsumption:total', ''),
('urn:oc:attributetype:illuminance', '', 'illuminance', ''),
('urn:oc:attributetype:luminosity', '', 'luminosity', ''),
('urn:oc:attributetype:mass', '', 'mass', ''),
('urn:oc:attributetype:mileage:distanceToService', '', 'mileage:distanceToService', ''),
('urn:oc:attributetype:mileage:total', '', 'mileage:total', ''),
('urn:oc:attributetype:motionState:vehicle', '', 'motionState:vehicle', ''),
('urn:oc:attributetype:odometer', '', 'odometer', ''),
('urn:oc:attributetype:position:altitude', '', 'position:altitude', ''),
('urn:oc:attributetype:position:latitude', '', 'position:latitude', ''),
('urn:oc:attributetype:position:longitude', '', 'position:longitude', ''),
('urn:oc:attributetype:presenceState:emergencyVehicle', '', 'presenceState:emergencyVehicle', ''),
('urn:oc:attributetype:presenceState:parking', '', 'presenceState:parking', ''),
('urn:oc:attributetype:presenceState:people', '', 'presenceState:people', ''),
('urn:oc:attributetype:atmosphericPressure', '', 'atmosphericPressure', ''),
('urn:oc:attributetype:rainfall', '', 'rainfall', ''),
('urn:oc:attributetype:relativeHumidity', '', 'relativeHumidity', ''),
('urn:oc:attributetype:roadOccupancy', '', 'roadOccupancy', ''),
('urn:oc:attributetype:rotationalSpeed:engine', '', 'rotationalSpeed:engine', ''),
('urn:oc:attributetype:soilMoistureTension', '', 'soilMoistureTension', ''),
('urn:oc:attributetype:solarRadiation', '', 'solarRadiation', ''),
('urn:oc:attributetype:soundPressureLevel:ambient', '', 'soundPressureLevel:ambient', ''),
('urn:oc:attributetype:speed:average', '', 'speed:average', ''),
('urn:oc:attributetype:temperature:ambient', '', 'temperature:ambient', ''),
('urn:oc:attributetype:temperature:device', '', 'temperature:device', ''),
('urn:oc:attributetype:temperature:engine', '', 'temperature:engine', ''),
('urn:oc:attributetype:temperature:soil', '', 'temperature:soil', ''),
('urn:oc:attributetype:temperature:wasteContainer', '', 'temperature:wasteContainer', ''),
('urn:oc:attributetype:trafficCongestion', '', 'trafficCongestion', ''),
('urn:oc:attributetype:trafficIntensity', '', 'trafficIntensity', ''),
('urn:oc:attributetype:vehicleOverspeedState', '', 'vehicleOverspeedState', ''),
('urn:oc:attributetype:windDirection', '', 'windDirection', ''),
('urn:oc:attributetype:windSpeed', '', 'windSpeed', ''),
('urn:oc:attributetype:temperature', '', 'temperature', ''),
('urn:oc:attributetype:humidity', '', 'humidity', ''),
('urn:oc:attributetype:location', '', 'location', ''),
('urn:oc:attributetype:speed:limit', '', 'speed:limit', '');


INSERT INTO PUBLIC.OCATTRIBUTE_TYPE_UNITS(ATTRIBUTES_URN, UNITS_URN) VALUES

('urn:oc:attributetype:batteryLevel', 'urn:oc:uom:percent'),
('urn:oc:attributetype:chemicalAgentAtmosphericConcentration:airParticles', 'urn:oc:uom:milligramPerCubicMetre'),
('urn:oc:attributetype:chemicalAgentAtmosphericConcentration:CO', 'urn:oc:uom:milligramPerCubicMetre'),
('urn:oc:attributetype:chemicalAgentAtmosphericConcentration:NO2', 'urn:oc:uom:microgramPerCubicMetre'),
('urn:oc:attributetype:chemicalAgentAtmosphericConcentration:O3', 'urn:oc:uom:microgramPerCubicMetre'),
('urn:oc:attributetype:chemicalAgentAtmosphericConcentration:SO2', 'urn:oc:uom:microgramPerCubicMetre'),
('urn:oc:attributetype:direction:azimuth', 'urn:oc:uom:degreeAngle'),
('urn:oc:attributetype:direction:heading', 'urn:oc:uom:index'),
('urn:oc:attributetype:electricField:1800mhz', 'urn:oc:uom:millivoltPerMetre'),
('urn:oc:attributetype:electricField:2100mhz', 'urn:oc:uom:millivoltPerMetre'),
('urn:oc:attributetype:electricField:2400mhz', 'urn:oc:uom:millivoltPerMetre'),
('urn:oc:attributetype:electricField:900mhz', 'urn:oc:uom:millivoltPerMetre'),
('urn:oc:attributetype:fillLevel:wasteContainer', 'urn:oc:uom:percent'),
('urn:oc:attributetype:fuelConsumption:instantaneous', 'urn:oc:uom:litrePer100Kilometres'),
('urn:oc:attributetype:fuelConsumption:total', 'urn:oc:uom:litre'),
('urn:oc:attributetype:illuminance', 'urn:oc:uom:lux'),
('urn:oc:attributetype:luminosity', 'urn:oc:uom:lumen'),
('urn:oc:attributetype:mass', 'urn:oc:uom:kilogram'),
('urn:oc:attributetype:mileage:distanceToService', 'urn:oc:uom:kilometre'),
('urn:oc:attributetype:mileage:total', 'urn:oc:uom:metre'),
('urn:oc:attributetype:motionState:vehicle', 'urn:oc:uom:index'),
('urn:oc:attributetype:odometer', 'urn:oc:uom:kilometre'),
('urn:oc:attributetype:position:altitude', 'urn:oc:uom:metre'),
('urn:oc:attributetype:position:latitude', 'urn:oc:uom:degreeAngle'),
('urn:oc:attributetype:position:longitude', 'urn:oc:uom:degreeAngle'),
('urn:oc:attributetype:presenceState:emergencyVehicle', 'urn:oc:uom:index'),
('urn:oc:attributetype:presenceState:parking', 'urn:oc:uom:index'),
('urn:oc:attributetype:presenceState:people', 'urn:oc:uom:index'),
('urn:oc:attributetype:atmosphericPressure', 'urn:oc:uom:bar'),
('urn:oc:attributetype:rainfall', 'urn:oc:uom:millimetrePerHour'),
('urn:oc:attributetype:relativeHumidity', 'urn:oc:uom:percent'),
('urn:oc:attributetype:roadOccupancy', 'urn:oc:uom:percent'),
('urn:oc:attributetype:rotationalSpeed:engine', 'urn:oc:uom:revolutionPerMinute'),
('urn:oc:attributetype:soilMoistureTension', 'urn:oc:uom:centibar'),
('urn:oc:attributetype:solarRadiation', 'urn:oc:uom:wattPerSquareMetre'),
('urn:oc:attributetype:soundPressureLevel:ambient', 'urn:oc:uom:decibel'),
('urn:oc:attributetype:speed:average', 'urn:oc:uom:kilometrePerHour'),
('urn:oc:attributetype:temperature:ambient', 'urn:oc:uom:degreeCelsius'),
('urn:oc:attributetype:temperature:device', 'urn:oc:uom:degreeCelsius'),
('urn:oc:attributetype:temperature:engine', 'urn:oc:uom:degreeCelsius'),
('urn:oc:attributetype:temperature:soil', 'urn:oc:uom:degreeCelsius'),
('urn:oc:attributetype:temperature:wasteContainer', 'urn:oc:uom:degreeCelsius'),
('urn:oc:attributetype:trafficCongestion', 'urn:oc:uom:index'),
('urn:oc:attributetype:trafficIntensity', 'urn:oc:uom:index'),
('urn:oc:attributetype:trafficIntensity', 'urn:oc:uom:vehiclePerMinute'),
('urn:oc:attributetype:vehicleOverspeedState', 'urn:oc:uom:index'),
('urn:oc:attributetype:windDirection', 'urn:oc:uom:degreeAngle'),
('urn:oc:attributetype:windSpeed', 'urn:oc:uom:kilometrePerHour'),
('urn:oc:attributetype:temperature', 'urn:oc:uom:degreeCelsius'),
('urn:oc:attributetype:humidity', 'urn:oc:uom:percent'),
('urn:oc:attributetype:speed:limit', 'urn:oc:uom:kilometrePerHour');


INSERT INTO PUBLIC.OCASSET_TYPE(URN, DESCRIPTION, NAME, RELATED) VALUES

('urn:oc:entitytype:districtprofile', '', 'districtprofile', ''),
('urn:oc:entitytype:iotdevice', '', 'iotdevice', ''),
('urn:oc:entitytype:weatherstation', '', 'weatherstation', ''),
('urn:oc:entitytype:trafficstats', '', 'trafficstats', ''),
('urn:oc:entitytype:busstation', '', 'busstation', ''),
('urn:oc:entitytype:trainstation', '', 'trainstation', ''),
('urn:oc:entitytype:tramstation', '', 'tramstation', ''),
('urn:oc:entitytype:undergroundstation', '', 'undergroundstation', ''),
('urn:oc:entitytype:environmentalstation', '', 'environmentalstation', ''),
('urn:oc:entitytype:shelter', '', 'shelter', ''),
('urn:oc:entitytype:bikeStation', '', 'bikeStation', ''),
('urn:oc:entitytype:shop', '', 'shop', ''),
('urn:oc:entitytype:busLine', '', 'busLine', ''),
('urn:oc:entitytype:taxiStation', '', 'taxiStation', ''),
('urn:oc:entitytype:district', '', 'district', ''),
('urn:oc:entitytype:section', '', 'section', ''),
('urn:oc:entitytype:event:culture', '', 'event:culture', ''),
('urn:oc:entitytype:event:strike', '', 'event:strike', ''),
('urn:oc:entitytype:iotdevice:luminosity', '', 'iotdevice:luminosity', ''),
('urn:oc:entitytype:iotdevice:noise', '', 'iotdevice:noise', ''),
('urn:oc:entitytype:iotdevice:vehiclecount', '', 'iotdevice:vehiclecount', ''),
('urn:oc:entitytype:iotdevice:vehiclespeed', '', 'iotdevice:vehiclespeed', ''),
('urn:oc:entitytype:iotdevice:agriculture', '', 'iotdevice:agriculture', ''),
('urn:oc:entitytype:iotdevice:irrigation', '', 'iotdevice:irrigation', ''),
('urn:oc:entitytype:iotdevice:magneticLoop', '', 'iotdevice:magneticLoop', ''),
('urn:oc:entitytype:trafficStatus', '', 'trafficStatus', ''),
('urn:oc:entitytype:iotdevice:tsmart', '', 'iotdevice:tsmart', ''),
('urn:oc:entitytype:speed:limit', '', 'speed:limit', ''),
('urn:oc:entitytype:pollutionsensor', '', 'pollutionsensor', ''),
('urn:oc:entitytype:busRoute', '', 'busRoute', ''),
('urn:oc:entitytype:parkingSpot', 'Parking Spot', 'parkingSpot', ''),
('urn:oc:entitytype:disParkingSpot', 'Disabled Person Parking Spot', 'disParkingSpot', '');


INSERT INTO PUBLIC.OCASSET_TYPE_ATTRIBUTES(ASSETS_URN, ATTRIBUTES_URN) VALUES

('urn:oc:entitytype:shop', 'urn:oc:attributetype:location');


INSERT INTO PUBLIC.OCAPP_TYPE(URN, DESCRIPTION, NAME) VALUES

('urn:oc:apptype:desktop', 'desktop application type', 'desktop'),
('urn:oc:apptype:mobile', 'mobile application type', 'mobile'),
('urn:oc:apptype:web', 'web application type', 'web'),
('urn:oc:apptype:oppnetlegacy', 'oppnet legacy application type', 'oppnetlegacy'),
('urn:oc:apptype:smartphone', 'smartphone experiment type', 'smartphone');

