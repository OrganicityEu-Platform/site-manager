package fr.cea.organicity.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.cea.organicity.manager.domain.OCApiCall;

public interface OCApiCallRepository extends JpaRepository<OCApiCall, Long> {
}
