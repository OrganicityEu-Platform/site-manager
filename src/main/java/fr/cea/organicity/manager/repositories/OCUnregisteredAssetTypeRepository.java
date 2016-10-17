package fr.cea.organicity.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.cea.organicity.manager.domain.OCUnregisteredAssetType;

public interface OCUnregisteredAssetTypeRepository extends JpaRepository<OCUnregisteredAssetType, String> {	
}
