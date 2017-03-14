package fr.cea.organicity.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.cea.organicity.manager.domain.OCService;

@Repository
public interface OCServiceRepository extends JpaRepository<OCService, String> {
}
