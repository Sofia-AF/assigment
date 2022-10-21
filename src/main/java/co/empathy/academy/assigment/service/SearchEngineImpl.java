package co.empathy.academy.assigment.service;

import co.empathy.academy.assigment.model.SimpleResponse;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class SearchEngineImpl implements SearchEngine {

    // This method should make a request to Elasticsearch to retrieve search results
    // For our example we'll just return the query length as number of results

    @Autowired
    private RestClient elasticService;

    @Override
    public int search(String query) {
        if (query == null) {
            throw new RuntimeException("Query is mandatory");
        }
        return query.length();
    }

    @Override
    public SimpleResponse searchQuery(String query) {
        String numberVersion = getElasticVersion();
        return new SimpleResponse(query, numberVersion);
    }

    public String getElasticVersion(){
        String numberVersion;
        try {
            Response response = elasticService.performRequest(new Request("GET","/"));
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(responseBody);
            JSONObject version = json.getJSONObject("version");
            numberVersion = version.getString("number");
        } catch (IOException e) {
            throw new RuntimeException(e);
        };
        return numberVersion;
    }

    public String getQueryValue(String query){
        return query;
    }


}
