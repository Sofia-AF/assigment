package co.empathy.academy.assigment.elastic.search;

import co.empathy.academy.assigment.model.Aka;
import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.model.Director;
import co.empathy.academy.assigment.model.SearchResponseCustom;
import co.empathy.academy.assigment.services.SearchEngine;
import co.empathy.academy.assigment.services.SearchService;
import co.empathy.academy.assigment.services.SearchServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class SearchServiceImplTest {

    private final String INDEX_TEST = "test";
    private final String BODY_TEST = "{}";
    private final String BAD_QUERY = "{\n" +
            "  \"query\": {\n" +
            "    \"bad\": {\n" +
            "      \n" +
            "\t\t}\n" +
            "\t} \n" +
            "}";
    private final String OK_REQUEST = "{\n" +
            "  \"query\": {\n" +
            "    \"multi_match\" : {\n" +
            "      \"query\":    \"Carmencita\",\n" +
            "      \"fields\": [ \"primaryTitle\", \"originalTitle\" ]\n" +
            "    }\n" +
            "\t} \n" +
            "}";

    private final List<Aka> akas = new ArrayList<>();
    private final List<Director> directors = new ArrayList<>(){{
        add(new Director("p1"));
    }};
    private final List<String> genres = new ArrayList<>(){{
        add("Documentaty");
        add("Short");
    }};
    private final Movie movie1 = new Movie("tt0000001", "short", "Carmencita", "Carmencita", true, 1894, 0, 1, genres, 9.1f, 100, akas, directors);
    private final List<Movie> movies = new ArrayList<>(){{
        add(movie1);
    }};
    SearchEngine searchEngine = mock(SearchEngine.class);
    SearchService searchService = new SearchServiceImpl(searchEngine);

    @Test
    void givenValidQuery_whenSearch_thenReturnBasicResponseFound(){
        String query = "shirt";
        String version = "7.17.2";
        SearchResponseCustom expectedResponse = new SearchResponseCustom(query, version);
        try {
            given(searchEngine.searchQuery(query)).willReturn(expectedResponse);
            SearchResponseCustom givenResponse = searchService.searchQuery(query);
            assertEquals(expectedResponse, givenResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void givenNotValidQuery_whenSearch_thenReturnError(){

        Throwable expectedException = new RuntimeException("Error while searching");
        try {
            given(searchEngine.searchQuery(null)).willThrow(expectedException);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertThrows(expectedException.getClass(), () -> searchService.searchQuery(null));
    }

    @Test
    void givenNullIndex_whenSearchIndex_thenReturnError(){
        Throwable expectedException = new IllegalArgumentException("ERROR: missing required parameter <indexName>");
        given(searchEngine.searchIndex(null, BODY_TEST)).willThrow(expectedException);
        assertThrows(expectedException.getClass(), () -> searchService.searchIndex(null, BODY_TEST));
    }

    @Test
    void givenNullBody_whenSearchIndex_thenReturnError(){
        Throwable expectedException = new IllegalArgumentException("ERROR: missing JSON body in PUT request");
        given(searchEngine.searchIndex(INDEX_TEST, null)).willThrow(expectedException);
        assertThrows(expectedException.getClass(), () -> searchService.searchIndex(INDEX_TEST, null));
    }

    @Test
    void givenBadRequest_whenSearchIndex_thenReturnError(){
        Throwable expectedException = new IllegalArgumentException("ERROR: query search not allowed");
        given(searchEngine.searchIndex(INDEX_TEST, BAD_QUERY)).willThrow(expectedException);
        assertThrows(expectedException.getClass(), () -> searchService.searchIndex(INDEX_TEST, BAD_QUERY));
    }

    @Test
    void givenOkRequest_whenSearchIndex_thenReturnOk(){
        given(searchEngine.searchIndex(INDEX_TEST, OK_REQUEST)).willReturn(movies);
        assertEquals(searchService.searchIndex(INDEX_TEST, OK_REQUEST), movies);
    }

}
