package cz.neumimto.core;

import org.hibernate.SessionFactory;
import org.spongepowered.api.event.Event;

/**
 * Created by NeumimTo on 28.11.2015.
 */
public final class SessionFactoryCreatedEvent implements Event {
    private final SessionFactory sessionFactory;

    public SessionFactoryCreatedEvent(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
