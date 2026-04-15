package hexlet.code.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Url {
    private Integer id;
    private final String name;
    private LocalDateTime createdAt;

    public String getCreatedTimeFormatted(String language) {
        return this.createdAt.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(Locale.forLanguageTag(language)));
    }
}
