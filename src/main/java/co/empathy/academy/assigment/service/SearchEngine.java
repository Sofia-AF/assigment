package co.empathy.academy.assigment.service;

import co.empathy.academy.assigment.model.SearchResponse;

import java.io.IOException;

public interface SearchEngine {

    int search(String query);
    SearchResponse searchQuery(String query) throws IOException;
}
