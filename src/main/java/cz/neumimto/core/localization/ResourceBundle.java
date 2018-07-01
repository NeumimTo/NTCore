package cz.neumimto.core.localization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by NeumimTo on 1.7.2018.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceBundle {
    String value();
}
