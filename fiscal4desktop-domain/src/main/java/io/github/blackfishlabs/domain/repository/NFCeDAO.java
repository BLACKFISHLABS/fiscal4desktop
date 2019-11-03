package io.github.blackfishlabs.domain.repository;

import io.github.blackfishlabs.domain.HibernateUtil;
import io.github.blackfishlabs.domain.model.NFCeEntity;

public class NFCeDAO extends GenericDAO<NFCeEntity> {

    public NFCeDAO() {
        super(HibernateUtil.getSessionFactory().getCurrentSession(), NFCeEntity.class);
    }
}
