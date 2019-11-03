package io.github.blackfishlabs.domain.repository;

import io.github.blackfishlabs.domain.HibernateUtil;
import io.github.blackfishlabs.domain.model.MDFeEntity;

public class MDFeDAO extends GenericDAO<MDFeEntity> {

    public MDFeDAO() {
        super(HibernateUtil.getSessionFactory().getCurrentSession(), MDFeEntity.class);
    }
}
