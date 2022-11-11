package co.empathy.academy.assigment.services;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.empathy.academy.assigment.model.Movie;
import org.json.JSONObject;

import java.util.List;

public interface QueryService {
    List<Movie> makeMultiMatchQuery(String indexName, JSONObject typeJSON);

    List<Movie> makeTermQuery(String indexName, JSONObject typeJSON);

    List<Movie> makeTermsQuery(String indexName, JSONObject typeJSON);
}
