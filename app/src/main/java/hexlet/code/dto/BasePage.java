package hexlet.code.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class BasePage {

    private String flash;
    private String alertType;

    @SuppressWarnings(value = "checkstyle:VisibilityModifier")
    protected String name = "";
}
