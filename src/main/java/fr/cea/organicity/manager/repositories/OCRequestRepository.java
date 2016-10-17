package fr.cea.organicity.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.cea.organicity.manager.domain.OCRequest;

public interface OCRequestRepository extends JpaRepository<OCRequest, Long> {

}
