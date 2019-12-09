package io.github.blackfishlabs.common.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class GenericService<T, PK extends Serializable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericService.class);

    @Autowired
    private JpaRepository<T, PK> genericRepository;

    public List<T> findAll() {
        LOGGER.info("Requesting all records.");
        return this.genericRepository.findAll();
    }

    public Page<T> findAll(Pageable pageable) {
        return this.genericRepository.findAll(pageable);
    }

    public long count() {
        return this.genericRepository.count();
    }

    public Optional<T> findOne(PK id) {
        LOGGER.info(String.format("Requesting the entity id [%s].", id));
        return this.genericRepository.findById(id);
    }

    public T insert(T entity) {
        LOGGER.info(String.format("Saving the entity [%s].", entity));
        return this.genericRepository.save(entity);
    }

    public T update(T entity) {
        LOGGER.info(String.format("Request to update the record [%s].", entity));
        return this.genericRepository.save(entity);
    }

    public T delete(T entity) {
        LOGGER.info(String.format("Request to delete the record [%s].", entity));
        this.genericRepository.delete(entity);
        return entity;
    }

}
