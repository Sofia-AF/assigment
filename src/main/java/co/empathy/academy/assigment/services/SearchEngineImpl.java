package co.empathy.academy.assigment.services;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.model.SearchResponseCustom;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class SearchEngineImpl implements SearchEngine {

    // This method should make a request to Elasticsearch to retrieve search results
    // For our example we'll just return the query length as number of results
    @Autowired
    public ElasticEngine elasticEngine;

    @Autowired
    private QueryService queries;

    @Override
    public int search(String query) {
        if (query == null) {
            throw new RuntimeException("Query is mandatory");
        }
        return query.length();
    }

    @Override
    public SearchResponseCustom searchQuery(String query) throws IOException {
        String numberVersion = elasticEngine.getElasticVersion();
        return new SearchResponseCustom(query, numberVersion);
    }

    /**
     * Makes a query to a specific index
     * @param indexName : index where the search is going to take place
     * @param body : settings of the query
     * @return : SimpleResponse
     */
    @Override
    public List<Movie> searchIndex(String indexName, String body) {
        // First checks parameters
        if(indexName == null)
            throw new IllegalArgumentException("ERROR: missing required parameter <indexName>");
        else if (body == null)
            throw new IllegalArgumentException("ERROR: missing JSON body in PUT request.");


        JSONObject query = new JSONObject(body).getJSONObject("query");
        Iterator<String> keys = query.keys();
        String queryType = query.keys().next();
        JSONObject typeJSON = query.getJSONObject(queryType);

        if(queryType.equals("multi_match"))
            return queries.makeMultiMatchQuery(indexName, typeJSON);
        else if(queryType.equals("term"))
            return queries.makeTermQuery(indexName, typeJSON);
        else if(queryType.contains("terms"))
            return queries.makeTermsQuery(indexName, typeJSON);
        else
            throw new IllegalArgumentException("ERROR: query search not allowed");
    }
}
