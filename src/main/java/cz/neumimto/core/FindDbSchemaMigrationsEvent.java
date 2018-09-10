package cz.neumimto.core;

import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;

public class FindDbSchemaMigrationsEvent implements Event {

    private final PluginCore core;

    public FindDbSchemaMigrationsEvent(PluginCore core) {
        this.core = core;
    }

    @Override
    public Cause getCause() {
        return Cause.of(EventContext.empty(), core);
    }
}
