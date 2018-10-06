package cz.neumimto.core.localization;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import org.slf4j.Logger;
import org.spongepowered.api.text.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class LocalizationService {

    private final List<Class<?>> localizations = new ArrayList<>();
    @Inject
    private Logger logger;
    private Map<String, Object> bundle = new HashMap<>();

    public void registerClass(Class<?> clazz) {
        localizations.add(clazz);
    }

    public synchronized void loadResourceBundle(String path, Locale locale, ClassLoader classLoader) {
        ResourceBundle bundle = null;
        if (classLoader == null) {
            bundle = ResourceBundle.getBundle(path, locale);
        } else {
            bundle = ResourceBundle.getBundle(path, locale, classLoader);
        }
        Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            String s = keys.nextElement();
            this.bundle.put(s, bundle.getObject(s));
        }
        loadResourceBundle(bundle);
    }

    private synchronized void loadResourceBundle(ResourceBundle resourceBundle) {
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

    public Text getText(String key) {
        String s = (String) bundle.get(key);
        s = s == null ? "" : s;
        return TextHelper.parse(s);
    }

    public List<Text> getTextList(String key) {
        String[] stringArray = (String[]) bundle.get(key);
        if (stringArray == null) {
            return Collections.emptyList();
        }
        return Stream.of(stringArray)
                .map(TextHelper::parse)
                .collect(Collectors.toList());
    }
}
