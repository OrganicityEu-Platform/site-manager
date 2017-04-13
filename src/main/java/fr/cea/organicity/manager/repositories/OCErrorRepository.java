package fr.cea.organicity.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.cea.organicity.manager.domain.OCError;

public interface OCErrorRepository extends JpaRepository<OCError, Long> {
}
