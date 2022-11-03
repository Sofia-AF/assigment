package co.empathy.academy.assigment.service;

import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.model.SimpleResponse;

import java.io.IOException;

public class ElasticServiceImpl implements ElasticService{

    private final ElasticEngine elasticEngine;

    public ElasticServiceImpl(ElasticEngine elasticEngine) {
        this.elasticEngine = elasticEngine;
    }

    @Override
    public String getElasticVersion() throws IOException {
        return elasticEngine.getElasticVersion();
    }

    @Override
    public SimpleResponse showAllIndexes() {
        return elasticEngine.showAllIndexes();
    }

    @Override
    public SimpleResponse createIndex(String indexName, String body) {
        return elasticEngine.createIndex(indexName, body);
    }

    @Override
    public SimpleResponse indexDocument(String indexName, String docId, Movie movie) {
        return elasticEngine.indexDocument(indexName, docId, movie);
    }
}
