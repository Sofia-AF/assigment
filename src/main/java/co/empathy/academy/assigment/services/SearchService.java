package co.empathy.academy.assigment.services;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.model.SearchResponseCustom;

import java.io.IOException;
import java.util.List;

public interface SearchService {

    int search(String query);
    SearchResponseCustom searchQuery(String query) throws IOException;

    List<Movie> searchIndex(String indexName, String body);
}
