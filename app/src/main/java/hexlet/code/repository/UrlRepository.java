package hexlet.code.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import hexlet.code.model.Url;

public class UrlRepository extends BaseRepository {

    public static void save(Url url) throws SQLException {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (var conn = getDataSource().getConnection();
                var statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, url.getName());
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            statement.executeUpdate();
            var generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                url.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("DB have not returned the record ID after saving an entity");
            }
        }
    }

    public static Optional<Url> find(Integer id) throws SQLException {
        var sql = "SELECT * FROM urls WHERE id = ?";
        try (var conn = getDataSource().getConnection(); var statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            var result = statement.executeQuery();

            return result.next() ? Optional.of(readOne(result)) : Optional.empty();
        }
    }

    public static Optional<Url> findByName(String name) throws SQLException {
        var sql = "SELECT * FROM urls WHERE name = ?";
        try (var conn = getDataSource().getConnection(); var statement = conn.prepareStatement(sql)) {
            statement.setString(1, name);
            var result = statement.executeQuery();

            return result.next() ? Optional.of(readOne(result)) : Optional.empty();
        }
    }

    public static List<Url> getEntities() throws SQLException {
        var sql = "SELECT * FROM urls";
        try (var conn = getDataSource().getConnection(); var statement = conn.prepareStatement(sql)) {
            var resultSet = statement.executeQuery();
            var result = new ArrayList<Url>();
            while (resultSet.next()) {
                result.add(readOne(resultSet));
            }

            return result;
        }
    }

    private static Url readOne(ResultSet result) throws SQLException {
        return new Url(result.getInt("id"), result.getString("name"),
                result.getTimestamp("created_at").toLocalDateTime());
    }
}
