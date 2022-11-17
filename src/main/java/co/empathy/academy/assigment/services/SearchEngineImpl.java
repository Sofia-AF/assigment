package co.empathy.academy.assigment.services;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.model.SearchResponseCustom;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class SearchEngineImpl implements SearchEngine {

    // This method should make a request to Elasticsearch to retrieve search results
    // For our example we'll just return the query length as number of results
    @Autowired
    public ElasticEngine elasticEngine;

    @Autowired
    private QueryService queries;

    /**
     * Gives the length of a query
     * @param query : given value of the query
     * @return int with the value
     */
    @Override
    public int simpleSearch(String query) {
        if (query == null) {
            throw new RuntimeException("Query is mandatory");
        }
        return query.length();
    }

    /**
     * Echoes the query with current ElasticSearch version
     * @param query : given query
     * @return : SearchResponseCustom with the info
     * @throws IOException : if can't connect with ElasticServer
     */
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

    /**
     * Custom search that filters with given parameters
     * @param genre : genre(s) of the movie
     * @param maxYear : upper range of the year interval
     * @param minYear : lower range of the year interval
     * @param maxMinutes : upper range of the runtimeMinutes interval
     * @param minMinutes : lower range of the runtimeMinutes interval
     * @param maxScore : upper range of the rate interval
     * @param minScore : lower range of the rate interval
     * @param type : if it's a movie, short or tv-series
     * @return : List of movies that satisfy the custom search
     */
    @Override
    public List<Movie> search(Optional<String> genre, Optional<Integer> maxYear, Optional<Integer> minYear,
                               Optional<Integer> maxMinutes, Optional<Integer> minMinutes,
                               Optional<Double> maxScore, Optional<Double> minScore, Optional<String> type) {
        
        List<Query> allQueries = new ArrayList<>();

        // Must be a movie or Tv-Series. Default value = "movie"
        if(type.isPresent())
            allQueries.add(queries.makeTermQueryMust("titleType", String.valueOf(type)));
        else
            allQueries.add(queries.makeTermQueryMust("titleType", "movie"));

        // It can't be an adult movie
        allQueries.add(queries.makeTermQueryMust("isAdult", "false"));

        // Should have one of these genres (or must?)
        if(genre.isPresent()){
            String[] genres = genre.get().split(",");
            allQueries.add(queries.makeTermsQueryShould("genres", genres));
        }

        // Must be in this year range
        if(maxYear.isPresent() && minYear.isPresent()){
            // It's for movies, so we use start year
            System.out.println(maxYear.get());
            System.out.println(minYear.get());
            allQueries.add(queries.makeRangeQuery("startYear",
                    maxYear.get(), minYear.get()));
        }

        // Must be in this runTime range
        if(maxMinutes.isPresent() && minMinutes.isPresent())
            allQueries.add(queries.makeRangeQuery("runtimeMinutes",
                    maxMinutes.get(),minMinutes.get()));

        // Must be in this ratingScore range
        if(maxScore.isPresent() && minScore.isPresent())
            allQueries.add(queries.makeRangeQuery("averageRating",
                    maxScore.get(),minScore.get()));

        // Make the final query with all the previous filters
        Query finalQuery = queries.filterAllQueries(allQueries);

        // Perform this custom query
        return queries.performQuery(finalQuery);
    }
}
