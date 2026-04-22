package hexlet.code.repository;

import com.zaxxer.hikari.HikariDataSource;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class BaseRepository {

    @Setter
    @Getter(AccessLevel.PROTECTED)
    private static HikariDataSource dataSource;
}
