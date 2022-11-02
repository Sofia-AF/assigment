package co.empathy.academy.assigment.service;

import co.empathy.academy.assigment.model.SearchResponse;

import java.io.IOException;

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
    public SearchResponse searchQuery(String query) throws IOException {
        return searchEngine.searchQuery(query);
    }

}
