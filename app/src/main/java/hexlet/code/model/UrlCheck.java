package hexlet.code.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UrlCheck {
    private Integer id;
    private final Integer urlId;
    private Integer statusCode;
    private String title;
    private String h1;
    private String description;
    private LocalDateTime createdAt;
}
