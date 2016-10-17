package fr.cea.organicity.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.cea.organicity.manager.domain.OCAppType;

public interface OCAppTypeRepository extends JpaRepository<OCAppType, String> {	
}
