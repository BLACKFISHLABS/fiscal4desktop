package io.github.blackfishlabs.fiscal4desktop.domain.repository;

import io.github.blackfishlabs.fiscal4desktop.domain.model.NFCeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NFCeRepository extends JpaRepository<NFCeEntity, String> {

    @Query(value = "select * from FISCAL_NFCE where key=:key AND emmiter=:emmiter", nativeQuery = true)
    List<NFCeEntity> search(@Param("key") String key, @Param("emmiter") String emitter);
}
