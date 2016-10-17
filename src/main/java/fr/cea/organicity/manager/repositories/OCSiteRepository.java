package fr.cea.organicity.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.cea.organicity.manager.domain.OCSite;

public interface OCSiteRepository extends JpaRepository<OCSite, String> {
}