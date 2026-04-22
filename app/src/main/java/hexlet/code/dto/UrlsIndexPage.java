package hexlet.code.dto;

import java.util.List;
import java.util.Map;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UrlsIndexPage extends BasePage {

    @SuppressWarnings(value = "checkstyle:VisibilityModifier")
    protected String name = "Ссылки";

    private final List<Url> urls;
    private final Map<Integer, UrlCheck> checks;
}
