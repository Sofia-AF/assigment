package co.empathy.academy.assigment.model;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Aka {
    private String title;
    private String region;
    private String language;
    private Boolean isOriginalTitle;
}
