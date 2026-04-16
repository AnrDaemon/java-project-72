package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

public class RootPage extends BasePage {

    protected String name = "Главная";

    @Getter
    @Setter
    private String url;
}
