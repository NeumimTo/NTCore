package cz.neumimto.core;

import org.hibernate.*;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.SynchronizationType;
import javax.persistence.criteria.CriteriaBuilder;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DummySessionFactory implements SessionFactory {
    @Override
    public SessionFactoryOptions getSessionFactoryOptions() {
        throw ex();
    }

    @Override
    public SessionBuilder withOptions() {
        throw ex();
    }

    @Override
    public Session openSession() throws HibernateException {
        throw ex();
    }

    @Override
    public Session getCurrentSession() throws HibernateException {
        throw ex();
    }

    @Override
    public StatelessSessionBuilder withStatelessOptions() {
        throw ex();
    }

    @Override
    public StatelessSession openStatelessSession() {
        throw ex();
    }

    @Override
    public StatelessSession openStatelessSession(Connection connection) {
        throw ex();
    }

    @Override
    public Statistics getStatistics() {
        throw ex();
    }

    @Override
    public void close() throws HibernateException {
        throw ex();
    }

    @Override
    public Map<String, Object> getProperties() {
        throw ex();
    }

    @Override
    public boolean isClosed() {
        throw ex();
    }

    @Override
    public Cache getCache() {
        throw ex();
    }

    @Override
    public PersistenceUnitUtil getPersistenceUnitUtil() {
        throw ex();
    }

    @Override
    public void addNamedQuery(String name, javax.persistence.Query query) {
        throw ex();
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        throw ex();
    }

    @Override
    public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
        throw ex();
    }

    @Override
    public Set getDefinedFilterNames() {
        throw ex();
    }

    @Override
    public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
        throw ex();
    }

    @Override
    public boolean containsFetchProfileDefinition(String name) {
        throw ex();
    }

    @Override
    public TypeHelper getTypeHelper() {
        throw ex();
    }

    @Override
    public ClassMetadata getClassMetadata(Class entityClass) {
        throw ex();
    }

    @Override
    public ClassMetadata getClassMetadata(String entityName) {
        throw ex();
    }

    @Override
    public CollectionMetadata getCollectionMetadata(String roleName) {
        throw ex();
    }

    @Override
    public Map<String, ClassMetadata> getAllClassMetadata() {
        throw ex();
    }

    @Override
    public Map getAllCollectionMetadata() {
        throw ex();
    }

    @Override
    public Reference getReference() throws NamingException {
        throw ex();
    }

    @Override
    public <T> List<EntityGraph<? super T>> findEntityGraphsByType(Class<T> entityClass) {
        throw ex();
    }

    @Override
    public EntityManager createEntityManager() {
        throw ex();
    }

    @Override
    public EntityManager createEntityManager(Map map) {
        throw ex();
    }

    @Override
    public EntityManager createEntityManager(SynchronizationType synchronizationType) {
        throw ex();
    }

    @Override
    public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
        throw ex();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        throw ex();
    }

    @Override
    public Metamodel getMetamodel() {
        throw ex();
    }

    @Override
    public boolean isOpen() {
        throw ex();
    }

    private RuntimeException ex() {
        return new RuntimeException("SessionFactory could not be initialized. " +
                "this error is not a main cause of your problems. " +
                "Before you ask for help DO NOT COPY THIS MESSAGE, its useless." +
                "The relevant error has happened during the server startup"
        );
    }
}
