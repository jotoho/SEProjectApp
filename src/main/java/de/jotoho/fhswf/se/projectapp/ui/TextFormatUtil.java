package de.jotoho.fhswf.se.projectapp.ui;

import static java.util.Objects.requireNonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.regex.Pattern;

public final class TextFormatUtil {
    private TextFormatUtil() {

    }

    public static String prepareText(@NotNull final String str,
                                     @Range(from = 1,
                                            to = Integer.MAX_VALUE) final int wrapAt,
                                     @Range(from = 0,
                                            to = Integer.MAX_VALUE) final int indent)
    {
        requireNonNull(str);
        final var pattern = Pattern.compile("(?<=^[^\\x0A]{" + wrapAt + "})(?!$)",
                                            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        String formattedOutline = str;
        for (var matcher = pattern.matcher(formattedOutline);
             matcher.find();
             matcher = pattern.matcher(formattedOutline)) {
            formattedOutline = matcher.replaceAll("\n");
        }
        formattedOutline = formattedOutline.indent(indent);
        return formattedOutline.stripTrailing();
    }
}
