package hexlet.code.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hexlet.code.model.UrlCheck;

public class UrlCheckRepository extends BaseRepository {

    @SuppressWarnings("checkstyle:MagicNumber")
    public static void save(UrlCheck urlCheck) throws SQLException {
        var sql = "INSERT INTO url_checks (url_id, status_code, h1, title, description, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (var statement = getDataSource().getConnection().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, urlCheck.getUrlId());
            statement.setInt(2, urlCheck.getStatusCode());
            statement.setString(3, urlCheck.getH1());
            statement.setString(4, urlCheck.getTitle());
            statement.setString(5, urlCheck.getDescription());
            statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            statement.executeUpdate();
            var generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("DB have not returned the record ID after saving an entity");
            }
        }
    }

    public static List<UrlCheck> findAllByUrlId(Integer urlId) throws SQLException {
        var sql = "SELECT * FROM url_checks WHERE url_id = ?";
        try (var statement = getDataSource().getConnection().prepareStatement(sql)) {
            statement.setInt(1, urlId);
            var resultSet = statement.executeQuery();
            var result = new ArrayList<UrlCheck>();
            while (resultSet.next()) {
                result.add(readOne(resultSet));
            }

            return result;
        }
    }

    public static Map<Integer, UrlCheck> findAllLatestChecks() throws SQLException {
        var sql = "WITH t AS (\n"
                + "SELECT id, created_at o, MAX(created_at) r FROM url_checks GROUP BY url_id\n"
                + ")\n"
                + "SELECT * FROM url_checks c\n"
                + "INNER JOIN t ON t.id = c.id AND t.o = t.r";
        try (var statement = getDataSource().getConnection().prepareStatement(sql)) {
            var resultSet = statement.executeQuery();
            var result = new HashMap<Integer, UrlCheck>();
            while (resultSet.next()) {
                var row = readOne(resultSet);
                result.put(row.getId(), row);
            }

            return result;
        }
    }

    private static UrlCheck readOne(ResultSet result) throws SQLException {
        return new UrlCheck(result.getInt("id"), result.getInt("url_id"), result.getInt("status_code"),
                result.getString("title"), result.getString("h1"), result.getString("description"),
                result.getTimestamp("created_at").toLocalDateTime());
    }
}
