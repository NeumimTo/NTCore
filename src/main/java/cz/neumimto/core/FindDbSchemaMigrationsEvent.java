package cz.neumimto.core;

import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;

public class FindDbSchemaMigrationsEvent implements Event {

    private final PluginCore core;
    private final String unit;

    public FindDbSchemaMigrationsEvent(PluginCore core, String unit) {
        this.core = core;
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public boolean validForContext(String str) {
        return unit.equals("*") || unit.equals(str);
    }

    @Override
    public Cause getCause() {
        return Cause.of(EventContext.empty(), core);
    }
}
