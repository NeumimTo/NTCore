package cz.neumimto.core.localization;

import org.spongepowered.api.text.Text;

/**
 * Created by NeumimTo on 23.6.2018.
 */
public class TextWrapper extends LocalizableParametrizedText {

    private Text text;

    public TextWrapper(Text text) {
        this.text = text;
    }

    @Override
    public Text toText() {
        return text;
    }

    @Override
    public Text toText(Arg arg) {
        return text;
    }
}
