package io.github.blackfishlabs.fiscal4desktop.domain.repository;

import io.github.blackfishlabs.fiscal4desktop.domain.model.ContingencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContingencyRepository extends JpaRepository<ContingencyEntity, String> {
}
