package hexlet.code.dto;

import java.util.List;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UrlsShowPage extends BasePage {
    private final Url url;
    private final List<UrlCheck> checks;
}
