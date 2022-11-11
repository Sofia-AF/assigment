package co.empathy.academy.assigment.services;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.model.SearchResponseCustom;

import java.io.IOException;
import java.util.List;

// This is the service class that will implement your search service logic
// It has a SearchEngine as a dependency
// Endpoint: /search (controller) -> SearchService -> SearchEngine
public class SearchServiceImpl implements SearchService {

    private final SearchEngine searchEngine;

    public SearchServiceImpl(SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
    }

    @Override
    public int search(String query) {
        return searchEngine.search(query);
    }

    @Override
    public SearchResponseCustom searchQuery(String query) throws IOException {
        return searchEngine.searchQuery(query);
    }

    @Override
    public List<Movie> searchIndex(String indexName, String body) {
        return searchEngine.searchIndex(indexName, body);
    }


}
