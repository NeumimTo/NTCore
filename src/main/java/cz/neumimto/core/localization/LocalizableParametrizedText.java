package cz.neumimto.core.localization;


import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class LocalizableParametrizedText {

    private String[] parts;
    private String[] args;

    public static LocalizableParametrizedText from(String string) {
        if (!string.contains("{{") && !string.contains("}}")) {
            return new TextWrapper(TextHelper.parse(string));
        }
        LocalizableParametrizedText text = new LocalizableParametrizedText();
        char[] chars = string.toCharArray();
        List<String> parts = new ArrayList<>();
        List<String> args = new ArrayList<>();
        int last = 0;
        for (int i = 1; i < chars.length; i++) {
            if (chars[i] == '{' && i + 3 < chars.length && chars[i - 1] == '{') {
                i++;
                String buffer = "";
                int start = i - 2;
                while (i < chars.length) {
                    if (chars[i] == '}' && chars[i + 1] == '}') {
                        i++;
                        break;
                    }
                    buffer += chars[i];
                    i++;
                }
                parts.add(string.substring(last, start));
                last = i + 1;
                args.add(string.substring(start + 2, i - 1));
            }
        }
        text.args = args.toArray(new String[args.size()]);
        text.parts = parts.toArray(new String[parts.size()]);
        return text;
    }

    public Text toText(Arg arg) {
        StringBuilder v = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            v.append(parts[i]).append(arg.getParams().get(args[i]));
            if (i + 1 == parts.length && args.length > i ) {
                i++;
                while (i < args.length) {
                    v.append(arg.getParams().get(args[i]));
                    i++;
                }
                break;
            }
        }
        return TextHelper.parse(v.toString());
    }


    public Text toText() {
        StringBuilder v = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            v.append(parts[i]);
        }
        return TextHelper.parse(v.toString());
    }
}
