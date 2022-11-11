package co.empathy.academy.assigment.services;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.model.SearchResponseCustom;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface SearchService {

    int simpleSearch(String query);
    SearchResponseCustom searchQuery(String query) throws IOException;
    List<Movie> searchIndex(String indexName, String body);

    List<Movie> search(Optional<String> genre, Optional<Integer> maxYear, Optional<Integer> minYear,
                          Optional<Integer> maxMinutes, Optional<Integer> minMinutes,
                          Optional<Double> maxScore, Optional<Double> minScore, Optional<String> type);
}
