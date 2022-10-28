package co.empathy.academy.assigment.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.empathy.academy.assigment.model.Movie;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Random;

public class ElasticClientImpl implements ElasticClient{
    @Autowired
    private ElasticsearchClient elastic;

    @Autowired
    private RestClient client;

    // Returns the current elastic search version as a String
    @Override
    public String getElasticVersion() {
        String numberVersion;
        try {
            Response response = client.performRequest(new Request("GET","/"));
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(responseBody);
            JSONObject version = json.getJSONObject("version");
            numberVersion = version.getString("number");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return numberVersion;
    }

    // Shows all indexes created
    @Override
    public String showAllIndexes() {
        String responseBody;
        try {
            Response response = client.performRequest(new Request("GET", "/_cat/indices/"));
            responseBody = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return responseBody;
    }

    // Creates a new index on indexName
    @Override
    public void createIndex(String indexName) {
        try {
            CreateIndexResponse createResponse = elastic.indices().create(
                    new CreateIndexRequest.Builder()
                            .index(indexName)
                            .build()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Index a document in a given index, and generates a random id for the mode
    @Override
    public void indexDocument(String indexName, Movie movie) {
        IndexResponse response = null;
        Random r = new Random();
        try {
            response = elastic.index(i -> i
                    .index(indexName)
                    .id(String.valueOf(r.nextInt(400) + 100))
                    .document(movie)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Indexed movie with version" + response.version());
    }

    // Index a document in a given index with a given id
    @Override
    public void indexDocumentWithId(String indexName, String movieId, Movie movie) {
        IndexResponse response = null;
        try {
            response = elastic.index(i -> i
                    .index(indexName)
                    .id(movieId)
                    .document(movie)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Indexed movie with version" + response.version());
    }
}
