package cz.neumimto.core;

import org.hibernate.SessionFactory;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

/**
 * Created by NeumimTo on 28.11.2015.
 */
public final class SessionFactoryCreatedEvent implements Event {
    private final SessionFactory sessionFactory;
    private Cause plugin = Cause.ofNullable(null);
    public SessionFactoryCreatedEvent(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public Cause getCause() {
        return plugin;
    }

    public void setCause(Object plugin) {
        this.plugin = Cause.of(plugin);
    }
}
