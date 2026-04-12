package hexlet.code.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Url {
    private Integer id;
    private final String name;
    private LocalDateTime createdAt;
}
