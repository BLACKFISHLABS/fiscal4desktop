package io.github.blackfishlabs.domain.repository;

import io.github.blackfishlabs.domain.HibernateUtil;
import io.github.blackfishlabs.domain.model.NFeEntity;

public class NFeDAO extends GenericDAO<NFeEntity> {

    public NFeDAO() {
        super(HibernateUtil.getSessionFactory().getCurrentSession(), NFeEntity.class);
    }
}
