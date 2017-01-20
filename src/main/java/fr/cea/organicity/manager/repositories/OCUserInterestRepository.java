package fr.cea.organicity.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.cea.organicity.manager.domain.OCUserInterest;

public interface OCUserInterestRepository extends JpaRepository<OCUserInterest, String> {
}
