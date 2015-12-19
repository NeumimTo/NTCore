package cz.neumimto.core.dao.genericDao;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.EntityManager;
import java.util.Set;

/**
 * Created by NeumimTo on 28.11.2015.
 */

/**
 * Generic DAO class template,
 * No locking is set up!
 * @param <E>
 */
public abstract class GenericDao<E> {

    @cz.neumimto.core.ioc.Inject
    protected SessionFactory factory;


    public void update(E e) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(e);
            session.flush();
            tx.commit();
        } catch (Exception ex) {
            if (tx!=null) tx.rollback();
            ex.printStackTrace();
        }finally {
            session.close();
        }
    }

    public void save(E e) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(e);
            session.flush();
            tx.commit();
        } catch (Exception ex) {
            if (tx!=null) tx.rollback();
            ex.printStackTrace();
        }finally {
            session.close();
        }
    }

    public void remove(E e) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(e);
            session.flush();
            tx.commit();
        } catch (Exception ex) {
            if (tx!=null) tx.rollback();
            ex.printStackTrace();
        }finally {
            session.close();
        }
    }

    public void merge(E e) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.merge(e);
            tx.commit();
        } catch (Exception ex) {
            if (tx!=null) tx.rollback();
            ex.printStackTrace();
        }finally {
            session.close();
        }
    }

}
