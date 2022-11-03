package co.empathy.academy.assigment.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.model.SimpleResponse;
import io.micrometer.core.instrument.util.IOUtils;
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

public class ElasticEngineImpl implements ElasticEngine {
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
     * @return : SimpleResponse where bodyMessage is a string with all the indexes found
     */
    @Override
    public SimpleResponse showAllIndexes() {
        String responseBody, endPoint = "/_cat/indices/";
        try {
            // Creates GET request to endPoint
            Response response = client.performRequest(new Request("GET", endPoint));
            responseBody = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            return new SimpleResponse(500, "ERROR: can't connect to server.");
        }
        return new SimpleResponse(200, responseBody);
    }

    /**
     * Creates a new index on <indexName>, with its settings and mappings stored in <body>
     * @param indexName : name to index to create
     * @param body      : retrieved String with the settings and
     * @return SimpleResponse
     */
    @Override
    public SimpleResponse createIndex(String indexName, String body) {
        // First checks parameters
        if(indexName == null)
            return new SimpleResponse(400, "ERROR: missing required parameter <indexName>");
        else if (body == null)
            return new SimpleResponse(400, "ERROR: missing JSON body in PUT request.");

        // Opens input stream for the string body
        InputStream inputBody = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));

        // Fills index request with the contents for the new indexName
        CreateIndexRequest request = CreateIndexRequest.of(b -> b
                .index(indexName).withJson(inputBody));

        try {
            // Creates the index, and checks it's been created correctly
            if(elastic.indices().create(request).acknowledged())
                return new SimpleResponse(200, " * Index '"+indexName+"' created correctly.");
        } catch (IOException e) {
            return new SimpleResponse(500, "ERROR: can't connect to server.");
        } catch (Exception e){
            return new SimpleResponse(400, "ERROR: index with <indexName> = '"+indexName+"', already exists.");
        }
        // Can't create index for unhandled reasons
        return new SimpleResponse(403, "ERROR: can't index '"+indexName+"'.");
    }

    /**
     * Indexes a new document in an index. If the target index does not exist, creates new one
     * @param indexName : index target to index the new document
     * @param docId : document identifier (optional)
     * @param movie : request body with info of the movie to index
     * @return SimpleResponse
     */
    @Override
    public SimpleResponse indexDocument(String indexName, String docId, Movie movie) {
        // First checks parameters
        if(indexName == null)
            return new SimpleResponse(400, "ERROR: missing required parameter <indexName>");
        else if (movie == null)
            return new SimpleResponse(400, "ERROR: missing JSON body in request.");

        try {
            // Checks type of request
            if(docId == null)
                // Post request
                elastic.index(i -> i.index(indexName).document(movie));
            else
                // Put request
                elastic.index(i -> i.index(indexName).id(docId).document(movie));
            return new SimpleResponse(200, "* Movie '" + movie.getTitle() + "', indexed correctly in '" + indexName + "'.");
        } catch (IOException e) {
            return new SimpleResponse(500, "ERROR: can't connect to server.");
        } catch (Exception e){
            // Can't index document for unhandled reasons
            return new SimpleResponse(403, "ERROR: can't index document in index '"+indexName+"'.");
        }

    }
}
