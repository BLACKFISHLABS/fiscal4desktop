package io.github.blackfishlabs.domain.repository;

import com.google.common.collect.Lists;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("unchecked")
public class GenericDAO<T extends Serializable> {

    private final Session session;
    private final Class<T> clazz;

    public GenericDAO(Session session, Class<T> clazz) {
        super();
        this.session = session;
        this.clazz = clazz;
    }

    public void save(T t) {
        this.session.beginTransaction();
        this.session.saveOrUpdate(t);
        this.session.getTransaction().commit();
    }

    public void update(T t) {
        Transaction tx = null;
        try {
            tx = this.session.beginTransaction();
            this.session.merge(t);
            tx.commit();
        } catch (RuntimeException e) {
            handleError(tx, e);
        }
    }

    private void handleError(Transaction tx, RuntimeException e) {
        if (tx != null && tx.isActive()) {
            try {
                tx.rollback();
            } catch (HibernateException e1) {
                throw e1;
            }
            throw e;
        }
    }

    public void delete(T t) {
        Transaction tx = null;
        try {
            tx = this.session.beginTransaction();
            this.session.delete(t);
            tx.commit();
        } catch (RuntimeException e) {
            handleError(tx, e);
        }
    }

    public List<T> find() {
        List<T> lista = Lists.newArrayList();

        Transaction tx = null;
        try {
            tx = this.session.beginTransaction();
            lista = this.session.createCriteria(clazz).list();
            tx.commit();
        } catch (HibernateException e) {
            handleError(tx, e);
        }
        return lista;

    }

    public List<T> filter(String query) {
        this.session.beginTransaction();
        Query hbQuery = session.createQuery(query);
        List<T> lista = hbQuery.list();
        this.session.getTransaction().commit();
        return lista;
    }
}