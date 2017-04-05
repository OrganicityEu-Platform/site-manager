package fr.cea.organicity.manager.controllers.ui;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import fr.cea.organicity.manager.domain.OCAppType;
import fr.cea.organicity.manager.domain.OCAssetType;
import fr.cea.organicity.manager.domain.OCAttributeType;
import fr.cea.organicity.manager.domain.OCDataType;
import fr.cea.organicity.manager.domain.OCTool;
import fr.cea.organicity.manager.domain.OCUnit;
import fr.cea.organicity.manager.domain.OCUnregisteredAssetType;
import fr.cea.organicity.manager.domain.OCUserInterest;
import fr.cea.organicity.manager.repositories.OCAppTypeRepository;
import fr.cea.organicity.manager.repositories.OCAssetTypeRepository;
import fr.cea.organicity.manager.repositories.OCAttributeTypeRepository;
import fr.cea.organicity.manager.repositories.OCDataTypeRepository;
import fr.cea.organicity.manager.repositories.OCToolRepository;
import fr.cea.organicity.manager.repositories.OCUnitRepository;
import fr.cea.organicity.manager.repositories.OCUnregisteredAssetTypeRepository;
import fr.cea.organicity.manager.repositories.OCUserInterestRepository;

@Component
public class DictionaryHelper {

	@Autowired private OCAssetTypeRepository assetTypeRepository;
	@Autowired private OCUnregisteredAssetTypeRepository unregisteredassetTypeRepository;
	@Autowired private OCAttributeTypeRepository attributeTypeRepository;
	@Autowired private OCUnitRepository unitRepository;
	@Autowired private OCDataTypeRepository datatypeRepository;
	@Autowired private OCToolRepository toolRepository;
	@Autowired private OCAppTypeRepository appTypeRepository;
	@Autowired private OCUserInterestRepository userInterestRepository;
	
	public List<OCAssetType> getAssetTypeRepository() {
		return getRepositoryContent(assetTypeRepository, (o1, o2) -> o1.getUrn().compareTo(o2.getUrn()));
	}
	
	public List<OCUnregisteredAssetType> getUnregisteredAssetTypeRepository() {
		return getRepositoryContent(unregisteredassetTypeRepository, (o1, o2) -> o1.getUrn().compareTo((o2).getUrn()));
	}
	
	public List<OCAttributeType> getAttributeTypeRepository() {
		return getRepositoryContent(attributeTypeRepository, (o1, o2) -> o1.getUrn().compareTo(o2.getUrn()));
	}
	
	public List<OCUnit> getUnitRepository() {
		return getRepositoryContent(unitRepository, (o1, o2) -> o1.getUrn().compareTo(o2.getUrn()));
	}
	
	public List<OCDataType> getDatatypeRepository() {
		return getRepositoryContent(datatypeRepository, (o1, o2) -> o1.getUrn().compareTo(o2.getUrn()));
	}
	
	public List<OCTool> getToolRepository() {
		return getRepositoryContent(toolRepository, (o1, o2) -> o1.getUrn().compareTo(o2.getUrn()));
	}
	
	public List<OCAppType> getAppTypeRepository() {
		return getRepositoryContent(appTypeRepository, (o1, o2) -> o1.getUrn().compareTo(o2.getUrn()));
	}

	public List<OCUserInterest> getUserInterestRepository() {
		return getRepositoryContent(userInterestRepository, (o1, o2) -> o1.getUrn().compareTo(o2.getUrn()));
	}
	
	/* ================ */
	/* HELPER FUNCTIONS */
	/* ================ */

	public static <T extends Object> List<T> getRepositoryContent(CrudRepository<T, String> repository, Comparator<? super T> comparator) {
		 return StreamSupport.stream(repository.findAll().spliterator(), false).sorted(comparator).collect(Collectors.toList());
	}
}
