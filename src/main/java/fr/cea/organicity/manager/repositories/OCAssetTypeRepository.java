package fr.cea.organicity.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.cea.organicity.manager.domain.OCAssetType;

public interface OCAssetTypeRepository extends JpaRepository<OCAssetType, String> {
}
