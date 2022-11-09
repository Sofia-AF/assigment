package co.empathy.academy.assigment.services;

import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.model.SimpleResponse;
import org.springframework.web.multipart.MultipartFile;

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

    @Override
    public SimpleResponse bulkIndex(MultipartFile basics, MultipartFile principals) {
       return elasticEngine.bulkIndex(basics, principals);
    }
}
