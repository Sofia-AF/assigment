package co.empathy.academy.assigment.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import co.empathy.academy.assigment.model.Movie;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QueryServiceImpl implements QueryService {
    @Autowired
    private ElasticsearchClient elastic;

    private final String IMDB_INDEX = "imdb";

    /**
     * Returns a list of Movie that satisfy the query
     * @param response : SearchResponse from de query petition
     * @return : list of movies with hits
     */
    public List<Movie> getFoundMovies(SearchResponse<Movie> response){
        List<Hit<Movie>> hits = response.hits().hits();
        List<Movie> movieHits = new ArrayList<>();
        for (Hit<Movie> hit: hits) {
            Movie movieFound = hit.source();
            //System.out.println(" * Found movie '" + movieFound.getOriginalTitle() + "', with score = " + hit.score());
            System.out.println(" * Found movie '" + movieFound.getOriginalTitle() + "' [" + movieFound.getTconst() + "]");
            movieHits.add(movieFound);
        }
        return movieHits;
    }

    /**
     * Makes a multi-match query
     *
     * @param indexName : index where the search is going to take place
     * @param typeJSON  : params of the query
     * @return list with found movies
     */
    @Override
    public List<Movie> makeMultiMatchQuery(String indexName, JSONObject typeJSON) {
        String query = typeJSON.getString("query");
        List<String> fields = new ArrayList<>();
        for(Object field : typeJSON.getJSONArray("fields"))
            fields.add(field.toString());

        try {
            MultiMatchQuery mm = MultiMatchQuery.of(m -> m.query(query).fields(fields));
            SearchResponse<Movie> response = elastic.search(s -> s.index(indexName).query(mm._toQuery())
                    .size(10), Movie.class);
            return getFoundMovies(response);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Makes a term query
     * @param indexName : index where the search is going to take place
     * @param typeJSON : params of the query
     * @return list with found movies
     */
    @Override
    public List<Movie> makeTermQuery(String indexName, JSONObject typeJSON){
        String term = (String) typeJSON.keys().next();
        JSONObject terms = typeJSON.getJSONObject(term);
        String value = terms.getString("value");
        String boost = terms.get("boost").toString();

        try {
            TermQuery tq = TermQuery.of(t -> t.field(term).value(value).boost(Float.valueOf(boost)));
            SearchResponse<Movie> response = elastic.search(s -> s.index(indexName).query(tq._toQuery())
                    .size(10), Movie.class);
            return getFoundMovies(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Makes a terms query
     * @param indexName : index where the search is going to take place
     * @param typeJSON : params of the query
     * @return list with found movies
     */
    @Override
    public List<Movie> makeTermsQuery(String indexName, JSONObject typeJSON){
        // NOT WORKING
        String field = (String) typeJSON.keys().next();
        String auxTerm = typeJSON.getJSONArray(field).toString();
        String[] terms = auxTerm.substring(1, auxTerm.length()-1).split(",");
        String boost = typeJSON.get("boost").toString();

        try {
            TermsQueryField termsField = TermsQueryField.of(t -> t
                    .value(Arrays.stream(terms).map(FieldValue::of)
                    .collect(Collectors.toList())));
            //TermsQueryBuilder tqb = new TermsQueryBuilder(field, terms);
            TermsQuery tq = TermsQuery.of(t -> t.terms(termsField).field(field).boost(Float.valueOf(boost)));
            SearchResponse<Movie> response = elastic.search(s -> s.index(indexName).query(tq._toQuery())
                    .size(10), Movie.class);
            return getFoundMovies(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *  Given all previous queries, creates one last query with all the filters
     * @param allQueries : list of all previous queries made
     * @return filtered query
     */
    @Override
    public Query filterAllQueries(List<Query> allQueries) {
        return BoolQuery.of(q -> q.filter(allQueries))._toQuery();
    }

    /**
     * Performs request given a query
     * @param finalQuery : filtered query
     * @return : list of movies that satisfy the finalQuery
     */
    @Override
    public List<Movie> performQuery(Query finalQuery) {
        try {
            SearchResponse<Movie> response = elastic.search(s -> s.index(IMDB_INDEX).query(finalQuery)
                    .size(100), Movie.class);
            // Returns 100 search hits
            return getFoundMovies(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates boolean query with should field
     * @param field : name of the field
     * @param terms : values of the terms in that given field
     * @return : created query
     */
    @Override
    public Query makeTermsQueryShould(String field, String[] terms) {
        return BoolQuery.of(q -> q
                     .should(s -> s
                         .terms(t -> t
                             .field(field)
                             .terms(tf -> tf
                                     .value(Arrays.stream(terms).map(FieldValue::of).toList())
                             )
                         )
                     )
                 )._toQuery();
    }

    /**
     * Creates boolean query with must field
     * @param field : name of the field
     * @param value : value of the field
     * @return : created query
     */
    @Override
    public Query makeTermQueryMust(String field, String value){
        return BoolQuery.of(q -> q
                    .must(m -> m
                        .term(t -> t
                            .field(field)
                            .value(value)
                )))._toQuery();
    }

    /**
     * Creates RangeQuery given max and min double values
     * @param field : name of the field
     * @param max : max value of the field
     * @param min : max value of the field
     * @return : created query
     */
    @Override
    public Query makeRangeQuery(String field, double max, double min) {
        return RangeQuery.of(q -> q
                        .field(field)
                        .gte(JsonData.of(min))  //greater than or equal
                        .lte(JsonData.of(max))) // less
                ._toQuery();
    }

    /**
     * Creates RangeQuery given max and min int values
     * @param field : name of the field
     * @param max : max value of the field
     * @param min : max value of the field
     * @return : created query
     */
    @Override
    public Query makeRangeQuery(String field, int max, int min) {
        return RangeQuery.of(q -> q
                    .field(field)
                    .gte(JsonData.of(min))
                    .lte(JsonData.of(max)))
                ._toQuery();
    }





}
