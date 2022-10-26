package co.empathy.academy.assigment.service;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class ElasticClientImpl implements ElasticClient{
    @Autowired
    private RestClient elastic;

    @Override
    public String getElasticVersion() {
        String numberVersion;
        try {
            Response response = elastic.performRequest(new Request("GET","/"));
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(responseBody);
            JSONObject version = json.getJSONObject("version");
            numberVersion = version.getString("number");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return numberVersion;
    }
}
