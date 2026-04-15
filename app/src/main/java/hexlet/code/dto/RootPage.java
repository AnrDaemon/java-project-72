package hexlet.code.dto;

import hexlet.code.model.Url;
import lombok.Getter;
import lombok.Setter;

public class RootPage extends BasePage {

    protected String name = "Главная";

    @Getter
    @Setter
    private Url url;
}
