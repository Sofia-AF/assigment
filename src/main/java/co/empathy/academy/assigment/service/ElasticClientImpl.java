package co.empathy.academy.assigment.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.empathy.academy.assigment.model.Movie;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class ElasticClientImpl implements ElasticClient{
    @Autowired
    private ElasticsearchClient elastic;

    @Autowired
    private RestClient client;


    /**
     * Returns the current ElasticSearch version as a String
     * @return : String with current ES version
     * @throws IOException : when can't perform GET request to ElasticSearch
     */
    @Override
    public String getElasticVersion() throws IOException {
        // Creates GET request to elasticSearch
        Response response = client.performRequest(new Request("GET","/"));
        // Retrieves info from cluster
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject json = new JSONObject(responseBody);
        JSONObject version = json.getJSONObject("version");
        // Gets current version from JSON response
        String numberVersion = version.getString("number");

        return numberVersion;
    }

    /**
     * Shows all indexes created
     * @return : String with all indexes found
     * @throws IOException : when can't perform GET request to ElasticSearch
     */
    @Override
    public String showAllIndexes() throws IOException {
        String responseBody, endPoint = "/_cat/indices/";
        // Creates GET request to endPoint
        Response response = client.performRequest(new Request("GET", endPoint));
        responseBody = EntityUtils.toString(response.getEntity());

        return responseBody;
    }

    /**
     * Creates a new index on <indexName>, with its settings and mappings stored in <body>
     * @param indexName : name to index to create
     * @param body : retrieved String with the settings and
     * @throws IOException : when can't perform index creation request to ElasticSearch
     */
    @Override
    public void createIndex(String indexName, String body) throws IOException {
        // Opens input stream for the string body
        InputStream inputBody = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));

        // Fills index request with the contents for the new indexName
        CreateIndexRequest request = CreateIndexRequest.of(b -> b
                .index(indexName).withJson(inputBody));
        // Creates the index, and checks it's been created correctly
        elastic.indices().create(request).acknowledged();


    }

    /**
     * Indexes a new document in an index. If the target index does not exist, creates new one
     * @param indexName : index target to index the new document
     * @param docId : document identifier (optional)
     * @param movie : request body with info of the movie to index
     * @throws IOException : when can't perform indexing request to ElasticSearch
     */
    @Override
    public void indexDocument(String indexName, String docId, Movie movie) throws IOException {
        // If no movieId is given, generates random id, range [100-500]
        String newId = ((docId == null) ? String.valueOf(new Random().nextInt(400) + 100) : docId);

        // IndexResponse response =
        elastic.index(i -> i.index(indexName).id(newId).document(movie));
        //System.out.println("Indexed movie with version" + response.version());
    }
}
