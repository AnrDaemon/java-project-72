package hexlet.code.util;

public class NamedRoutes {

    public static String root() {
        return "/";
    }

    public static String urlsRoot() {
        return "/urls";
    }

    public static String urlsUrl(Integer id) {
        return urlsUrl(String.valueOf(id));
    }

    public static String urlsUrl(String id) {
        return "/urls/" + id;
    }

    public static String urlsUrlCheck(Integer id) {
        return urlsUrlCheck(String.valueOf(id));
    }

    public static String urlsUrlCheck(String id) {
        return "/urls/" + id + "/checks";
    }
}
