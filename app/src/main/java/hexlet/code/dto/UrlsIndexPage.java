package hexlet.code.dto;

import hexlet.code.model.Url;

import java.util.List;
import java.util.Map;

import hexlet.code.model.UrlCheck;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class UrlsIndexPage extends BasePage {
    private List<Url> urls;
    private Map<Integer, UrlCheck> checks;
}
