package io.github.blackfishlabs.fiscal4desktop.domain.repository;

import io.github.blackfishlabs.fiscal4desktop.domain.model.NFeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NFeRepository extends JpaRepository<NFeEntity, String> {

    @Query(value = "select * from FISCAL_NFE where key=:key AND emmiter=:emmiter", nativeQuery = true)
    List<NFeEntity> search(@Param("key") String key, @Param("emmiter") String emitter);
}
