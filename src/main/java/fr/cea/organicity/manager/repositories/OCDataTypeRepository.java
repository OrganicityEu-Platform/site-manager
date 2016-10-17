package fr.cea.organicity.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.cea.organicity.manager.domain.OCDataType;

public interface OCDataTypeRepository extends JpaRepository<OCDataType, String> {
}
