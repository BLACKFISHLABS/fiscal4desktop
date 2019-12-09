package io.github.blackfishlabs.domain.repository;

import io.github.blackfishlabs.domain.model.MDFeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MDFeRepository extends JpaRepository<MDFeEntity, String> {

    @Query(value = "select * from FISCAL_MDFE nf where nf.KEY = :KEY AND nf.EMITTER = :EMITTER", nativeQuery = true)
    List<MDFeEntity> search(@Param("KEY") String key, @Param("EMITTER") String emitter);
}
