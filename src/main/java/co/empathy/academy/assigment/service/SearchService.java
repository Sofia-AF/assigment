package co.empathy.academy.assigment.service;

import co.empathy.academy.assigment.model.SimpleResponse;

public interface SearchService {

    int search(String query);
    SimpleResponse searchQuery(String query);

}
