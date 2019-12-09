package io.github.blackfishlabs.domain.repository;

import io.github.blackfishlabs.domain.model.ContingencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContingencyRepository extends JpaRepository<ContingencyEntity, String> {
}
