package fr.cea.organicity.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.cea.organicity.manager.domain.OCTool;

public interface OCToolRepository extends JpaRepository<OCTool, String> {
}
