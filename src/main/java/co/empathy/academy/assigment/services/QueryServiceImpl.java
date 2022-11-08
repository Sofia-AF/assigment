package co.empathy.academy.assigment.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.empathy.academy.assigment.model.Movie;
import org.elasticsearch.index.query.TermsQueryBuilder;
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
            System.out.println(" * Found movie '" + movieFound.getOriginalTitle() + "', with score = " + hit.score());
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
}
