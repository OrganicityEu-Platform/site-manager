package fr.cea.organicity.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.cea.organicity.manager.domain.OCService;

public interface OCServiceRepository extends JpaRepository<OCService, String> {
}
