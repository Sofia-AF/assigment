package co.empathy.academy.assigment.service;

import co.empathy.academy.assigment.model.SimpleResponse;

import java.io.IOException;

public interface SearchEngine {

    int search(String query);
    SimpleResponse searchQuery(String query) throws IOException;
}
