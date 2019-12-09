package io.github.blackfishlabs.domain.repository;

import io.github.blackfishlabs.domain.model.NFCeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NFCeRepository extends JpaRepository<NFCeEntity, String> {

    @Query(value = "select * from FISCAL_NFCE nf where nf.KEY = :KEY AND nf.EMITTER = :EMITTER", nativeQuery = true)
    List<NFCeEntity> search(@Param("KEY") String key, @Param("EMITTER") String emitter);
}
