package cz.neumimto.core;

import org.spongepowered.api.event.Event;

import javax.persistence.EntityManager;

/**
 * Created by NeumimTo on 28.11.2015.
 */
public final class EntityManagerCreatedEvent implements Event {
    private final EntityManager entityManager;

    public EntityManagerCreatedEvent(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
