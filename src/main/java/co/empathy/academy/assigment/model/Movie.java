package co.empathy.academy.assigment.model;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    private String tconst;
    private String titleType;
    private String primaryTitle;
    private String originalTitle;
    private Boolean isAdult;
    private int startYear;
    private int endYear;
    private int runtimeMinutes;
    private List<String> genres;
    private float averageRating;
    private int numVotes;
    private List<Aka> akas;
    private List<Principal> principals;
}
