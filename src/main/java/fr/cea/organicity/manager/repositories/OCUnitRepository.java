package fr.cea.organicity.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.cea.organicity.manager.domain.OCUnit;

public interface OCUnitRepository extends JpaRepository<OCUnit, String>{
}
