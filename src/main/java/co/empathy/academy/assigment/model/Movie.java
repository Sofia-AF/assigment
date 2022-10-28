package co.empathy.academy.assigment.model;
import lombok.Value;

@Value
public class Movie {
    private String title;
    private int year;
    private String genre;
    private String director;
}
