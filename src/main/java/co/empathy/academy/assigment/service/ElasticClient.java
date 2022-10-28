package co.empathy.academy.assigment.service;

import co.empathy.academy.assigment.model.Movie;

public interface ElasticClient {
    String getElasticVersion();

    String showAllIndexes();

    void createIndex(String indexName);

    void indexDocumentWithId(String indexName, String docId, Movie movie);

    void indexDocument(String indexName, Movie movie);
}
