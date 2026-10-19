package validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

// Accepts the 4 date formats from the teacher's notes:
//   29/9/2026   9/29/2026   9th Sep, 2026   Sep, 9, 2026
// If both day/month orders fit (e.g. 5/6/2026) day/month is used, like the data files.
public class DateValidator {

    public static final String ACCEPTED_FORMATS
            = "d/M/yyyy, M/d/yyyy, 9th Sep, 2026 or Sep, 9, 2026";

    private static final DateTimeFormatter[] FORMATS = {
        create("d/M/uuuu"),
        create("M/d/uuuu"),
        create("d MMM, uuuu"),
        create("MMM, d, uuuu")
    };

    private static DateTimeFormatter create(String pattern) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    // Returns the date, or null if the text is null, matches none of the formats, or is not a real date
    public static LocalDate parse(String text) {
        if (text == null) {
            return null;
        }
        // "9th Sep, 2026" -> "9 Sep, 2026"
        String cleaned = text.trim().replaceAll("(?i)(?<=\\d)(st|nd|rd|th)\\b", "");
        for (DateTimeFormatter format : FORMATS) {
            try {
                return LocalDate.parse(cleaned, format);
            } catch (DateTimeParseException e) {
                // try the next format
            }
        }
        return null;
    }
}
