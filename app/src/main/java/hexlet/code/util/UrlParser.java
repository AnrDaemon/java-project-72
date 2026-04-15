package hexlet.code.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public class UrlParser {
    public static String getNormalizedUrl(String url) throws MalformedURLException, URISyntaxException {
        var tmpUrl = new URI(url).toURL();
        var protocol = tmpUrl.getProtocol();
        var authority = tmpUrl.getAuthority();
        return protocol + "://" + authority;
    }
}
