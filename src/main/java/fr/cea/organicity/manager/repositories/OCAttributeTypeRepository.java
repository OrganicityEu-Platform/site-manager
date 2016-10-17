package fr.cea.organicity.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.cea.organicity.manager.domain.OCAttributeType;

public interface OCAttributeTypeRepository extends JpaRepository<OCAttributeType, String> {
}
