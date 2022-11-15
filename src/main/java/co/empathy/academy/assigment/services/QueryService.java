package co.empathy.academy.assigment.services;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.empathy.academy.assigment.model.Movie;
import org.json.JSONObject;

import java.util.List;

public interface QueryService {
    /**
     * Makes a multi-match query
     *
     * @param indexName : index where the search is going to take place
     * @param typeJSON  : params of the query
     * @return list with found movies
     */
    List<Movie> makeMultiMatchQuery(String indexName, JSONObject typeJSON);

    /**
     * Makes a term query
     * @param indexName : index where the search is going to take place
     * @param typeJSON : params of the query
     * @return list with found movies
     */
    List<Movie> makeTermQuery(String indexName, JSONObject typeJSON);

    /**
     * Makes a terms query
     * @param indexName : index where the search is going to take place
     * @param typeJSON : params of the query
     * @return list with found movies
     */
    List<Movie> makeTermsQuery(String indexName, JSONObject typeJSON);

    /**
     * Creates boolean query with should field
     * @param field : name of the field
     * @param terms : values of the terms in that given field
     * @return : created query
     */
    Query makeTermsQueryShould(String field, String[] terms);

    /**
     * Creates boolean query with must field
     * @param field : name of the field
     * @param value : value of the field
     * @return : created query
     */
    Query makeTermQueryMust(String field, String value);

    /**
     * Creates RangeQuery given max and min double values
     * @param field : name of the field
     * @param max : max value of the field
     * @param min : max value of the field
     * @return : created query
     */
    Query makeRangeQuery(String field, double max, double min);

    /**
     * Creates RangeQuery given max and min int values
     * @param field : name of the field
     * @param max : max value of the field
     * @param min : max value of the field
     * @return : created query
     */
    Query makeRangeQuery(String field, int max, int min);

    /**
     *  Given all previous queries, creates one last query with all the filters
     * @param allQueries : list of all previous queries made
     * @return filtered query
     */
    Query filterAllQueries(List<Query> allQueries);

    /**
     * Performs request given a query
     * @param finalQuery : filtered query
     * @return : list of movies that satisfy the finalQuery
     */
    List<Movie> performQuery(Query finalQuery);
}
