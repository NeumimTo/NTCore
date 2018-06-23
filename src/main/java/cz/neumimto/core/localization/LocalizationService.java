package cz.neumimto.core.localization;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import org.slf4j.Logger;
import org.spongepowered.api.text.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@Singleton
public class LocalizationService {

    @Inject
    private Logger logger;

    private final List<Class<?>> localizations = new ArrayList<>();

    public void registerClass(Class<?> clazz) {
        localizations.add(clazz);
    }


    public void loadResourceBundle(ResourceBundle resourceBundle) {
        for (Class<?> localization : localizations) {
            Field[] fields = localization.getFields();
            for (Field field : fields) {
                if (field.getType() == LocalizableParametrizedText.class && Modifier.isStatic(field.getModifiers())) {
                    String value = localization.getAnnotation(Localization.class).value();
                    value = value + "." + field.getName().toLowerCase();
                    String string = resourceBundle.getString(value);
                    if (string == null) {
                        logger.error("Missing translation in " + resourceBundle.getLocale() + "for string " + value);
                    } else {
                        try {
                            if (LocalizableParametrizedText.class.isAssignableFrom(field.getType())) {
                                field.set(null, LocalizableParametrizedText.from(string));
                            } else if (Text.class.isAssignableFrom(field.getType())) {
                                field.set(null, TextHelper.parse(string));
                            } else {
                                logger.error("Unknown type " + field.getType() + " for string " + value);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
