package io.github.blackfishlabs.domain.repository;

import io.github.blackfishlabs.domain.HibernateUtil;
import io.github.blackfishlabs.domain.model.ContingencyEntity;

public class ContigencyDAO extends GenericDAO<ContingencyEntity> {

    public ContigencyDAO() {
        super(HibernateUtil.getSessionFactory().getCurrentSession(), ContingencyEntity.class);
    }
}