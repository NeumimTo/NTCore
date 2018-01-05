package cz.neumimto.core;

import org.hibernate.SessionFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.impl.AbstractEvent;

/**
 * Created by NeumimTo on 28.11.2015.
 */
public final class SessionFactoryCreatedEvent extends AbstractEvent {
    private final SessionFactory sessionFactory;
    public SessionFactoryCreatedEvent(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }


    @Override
    public Cause getCause() {
        return Cause.of(EventContext.empty(), PluginCore.Instance);
    }
}
