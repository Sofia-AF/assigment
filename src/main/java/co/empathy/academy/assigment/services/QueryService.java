package co.empathy.academy.assigment.services;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.empathy.academy.assigment.model.Movie;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public interface QueryService {
    List<Movie> makeMultiMatchQuery(String indexName, JSONObject typeJSON);

    List<Movie> makeTermQueryOld(String indexName, JSONObject typeJSON);

    List<Movie> makeTermsQuery(String indexName, JSONObject typeJSON);

    Query makeTermQuery(String field, String value);
    List<Movie> makeIntervalQuery(String field, double max, double min);

    List<Movie> makeAllQueries(List<Query> queries) throws IOException;


    }
