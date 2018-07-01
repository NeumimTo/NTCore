package cz.neumimto.core.localization;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import org.slf4j.Logger;
import org.spongepowered.api.text.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

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
                    try {
                        if (LocalizableParametrizedText.class.isAssignableFrom(field.getType())) {
                            try {
                                String string = resourceBundle.getString(value);
                                field.set(null, LocalizableParametrizedText.from(string));
                            } catch (MissingResourceException e) {
                                logger.error("Missing translation in " + resourceBundle.getLocale() + "for string " + value);
                            }

                        } else if (Text.class.isAssignableFrom(field.getType())) {
                            try {
                                String string = resourceBundle.getString(value);
                                field.set(null, TextHelper.parse(string));
                            } catch (MissingResourceException e) {
                                logger.error("Missing translation in " + resourceBundle.getLocale() + "for string " + value);
                            }

                        } else if (List.class.isAssignableFrom(field.getType())) {
                            List<Text> text = new ArrayList<>();
                            try {
                                String[] stringArray = resourceBundle.getStringArray(value);
                                for (String s : stringArray) {
                                    text.add(TextHelper.parse(s));
                                }
                                field.set(null, text);
                            } catch (MissingResourceException e) {
                                logger.error("Missing translation in " + resourceBundle.getLocale() + "for string " + value);
                            }

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
