package co.empathy.academy.assigment.service;

import co.empathy.academy.assigment.model.SimpleResponse;

public interface SearchEngine {

    int search(String query);
    SimpleResponse searchQuery(String query);
    String getQueryValue(String query);
    String getElasticVersion();
}
