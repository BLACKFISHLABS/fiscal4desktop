package io.github.blackfishlabs.domain.repository;

import io.github.blackfishlabs.domain.model.NFeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NFeRepository extends JpaRepository<NFeEntity, String> {

    @Modifying
    @Query(value = "select * from FISCAL_NFE nf where nf.KEY=:key AND nf.EMITTER=:emitter", nativeQuery = true)
    List<NFeEntity> search(@Param("key") String key, @Param("emitter") String emitter);
}
