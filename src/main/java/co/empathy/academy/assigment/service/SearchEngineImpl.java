package co.empathy.academy.assigment.service;

import co.empathy.academy.assigment.model.SimpleResponse;
import org.springframework.beans.factory.annotation.Autowired;

public class SearchEngineImpl implements SearchEngine {

    // This method should make a request to Elasticsearch to retrieve search results
    // For our example we'll just return the query length as number of results
    @Autowired
    public ElasticClient elasticClient;

    @Override
    public int search(String query) {
        if (query == null) {
            throw new RuntimeException("Query is mandatory");
        }
        return query.length();
    }

    @Override
    public SimpleResponse searchQuery(String query) {
        String numberVersion = elasticClient.getElasticVersion();
        return new SimpleResponse(query, numberVersion);
    }
}
