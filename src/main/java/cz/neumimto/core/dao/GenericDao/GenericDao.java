package cz.neumimto.core.dao.genericDao;


import javax.persistence.EntityManager;

/**
 * Created by NeumimTo on 28.11.2015.
 */

public abstract class GenericDao<E> {

    @cz.neumimto.core.ioc.Inject
    protected EntityManager em;

    public void update(E e) {
        em.getTransaction().begin();
        em.merge(e);
        em.getTransaction().commit();
    }

    public void save(E e) {
        em.getTransaction().begin();
        em.persist(e);
        em.getTransaction().commit();
    }

    public void remove(E e) {
        em.getTransaction().begin();
        em.remove(e);
        em.getTransaction().commit();
    }


}
