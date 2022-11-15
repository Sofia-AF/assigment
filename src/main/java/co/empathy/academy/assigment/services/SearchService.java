package co.empathy.academy.assigment.services;

import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.model.SearchResponseCustom;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface SearchService {

    /**
     * Gives the length of a query
     * @param query : given value of the query
     * @return int with the value
     */
    int simpleSearch(String query);

    /**
     * Echoes the query with current ElasticSearch version
     * @param query : given query
     * @return : SearchResponseCustom with the info
     * @throws IOException : if can't connect with ElasticServer
     */
    SearchResponseCustom searchQuery(String query) throws IOException;

    /**
     * Makes a query to a specific index
     * @param indexName : index where the search is going to take place
     * @param body : settings of the query
     * @return : SimpleResponse
     */
    List<Movie> searchIndex(String indexName, String body);

    /**
     * Custom search that filters with given parameters
     * @param genre : genre(s) of the movie
     * @param maxYear : upper range of the year interval
     * @param minYear : lower range of the year interval
     * @param maxMinutes : upper range of the runtimeMinutes interval
     * @param minMinutes : lower range of the runtimeMinutes interval
     * @param maxScore : upper range of the rate interval
     * @param minScore : lower range of the rate interval
     * @param type : if it's a movie, short or tv-series
     * @return : List of movies that satisfy the custom search
     */
    List<Movie> search(Optional<String> genre, Optional<Integer> maxYear, Optional<Integer> minYear,
                          Optional<Integer> maxMinutes, Optional<Integer> minMinutes,
                          Optional<Double> maxScore, Optional<Double> minScore, Optional<String> type);

}
