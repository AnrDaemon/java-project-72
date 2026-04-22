package hexlet.code.util;

public class NamedRoutes {

    public static String root() {
        return "/";
    }

    public static String urlsRoot() {
        return "/urls";
    }

    public static String urlsRoot(Integer id) {
        return urlsRoot(String.valueOf(id));
    }

    public static String urlsRoot(String id) {
        return urlsRoot() + "/" + id;
    }

    public static String urlsUrlCheck(Integer id) {
        return urlsUrlCheck(String.valueOf(id));
    }

    public static String urlsUrlCheck(String id) {
        return urlsRoot(id) + "/checks";
    }
}
