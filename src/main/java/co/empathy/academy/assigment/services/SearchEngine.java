package co.empathy.academy.assigment.services;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.model.SearchResponseCustom;

import java.io.IOException;
import java.util.List;

public interface SearchEngine {

    int search(String query);
    SearchResponseCustom searchQuery(String query) throws IOException;

    /**
     * Makes a query to a specific index
     * @param indexName : index where the search is going to take place
     * @param body : settings of the query
     * @return : SimpleResponse
     */
    List<Movie> searchIndex(String indexName, String body);
}
