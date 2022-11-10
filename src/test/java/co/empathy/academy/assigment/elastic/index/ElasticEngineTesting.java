package co.empathy.academy.assigment.elastic.index;

import co.empathy.academy.assigment.model.Aka;
import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.model.Director;
import co.empathy.academy.assigment.model.SimpleResponse;
import co.empathy.academy.assigment.services.ElasticEngine;
import co.empathy.academy.assigment.services.ElasticEngineImpl;
import co.empathy.academy.assigment.services.ElasticService;
import co.empathy.academy.assigment.services.ElasticServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ElasticEngineTesting {
    private final ElasticEngine ee = mock(ElasticEngineImpl.class);
    private final int EXPECTED_SUCCESS_CODE = 200;
    private final int EXPECTED_ERROR_CODE = 400;
    private final String TEST_INDEX_NAME = "test_index";
    private final List<Aka> akas = new ArrayList<>();
    private final List<Director> directors = new ArrayList<>(){{
        add(new Director("p1"));
    }};
    private final List<String> genres = new ArrayList<>(){{
        add("Animation");
    }};
    private final Movie movie = new Movie("id1","Movie", "Cars", "Cars", false, 2006, 0, 120, genres, 7.1f, 100, akas, directors);


    @Test
    void givenValidRequest_whenShowAllResults_thenReturnOk(){
        String expectedBody = "green  open .geoip_databases _zvzsizZQ9iChtYCVp5vjw 1 0 40 40 38.3mb 38.3mb";
        SimpleResponse expected = new SimpleResponse(EXPECTED_SUCCESS_CODE, expectedBody);
        given(ee.showAllIndexes()).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.showAllIndexes();
        assertEquals(given.getBodyMessage(), expectedBody);
        assertEquals(given.getStatusCode(), EXPECTED_SUCCESS_CODE);
    }

    @Test
    void givenCorrectIndexParameters_whenCreateIndex_thenReturnOk(){
        String expectedBody = "* Index 'index_test' created correctly.";
        SimpleResponse expected = new SimpleResponse(EXPECTED_SUCCESS_CODE, expectedBody);
        given(ee.createIndex(TEST_INDEX_NAME,"")).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.createIndex(TEST_INDEX_NAME, "");
        assertEquals(given.getBodyMessage(), expectedBody);
        assertEquals(given.getStatusCode(), EXPECTED_SUCCESS_CODE);
    }

    @Test
    void givenBadIndexParameter_whenCreateIndex_thenReturnError400(){
        String expectedBody = "ERROR: missing required parameter <indexName>";
        SimpleResponse expected = new SimpleResponse(EXPECTED_ERROR_CODE, expectedBody);
        given(ee.createIndex(null,"")).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.createIndex(null, "");
        assertEquals(given.getBodyMessage(), expectedBody);
        assertEquals(given.getStatusCode(), EXPECTED_ERROR_CODE);
    }

    @Test
    void givenBadBodyParameter_whenCreateIndex_thenReturnError400(){
        String expectedBody = "ERROR: missing JSON body in PUT request.";
        SimpleResponse expected = new SimpleResponse(EXPECTED_ERROR_CODE, expectedBody);
        given(ee.createIndex(TEST_INDEX_NAME,null)).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.createIndex(TEST_INDEX_NAME, null);
        assertEquals(given.getBodyMessage(), expectedBody);
        assertEquals(given.getStatusCode(), EXPECTED_ERROR_CODE);
    }

    @Test
    void givenSameIndexDuplicate_whenCreateIndex_thenReturnError(){
        // First create the new index
        String expectedBody = "* Index 'index_test' created correctly.";
        SimpleResponse expected = new SimpleResponse(EXPECTED_SUCCESS_CODE, expectedBody);
        given(ee.createIndex(TEST_INDEX_NAME,"")).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.createIndex(TEST_INDEX_NAME, "");

        // Then index the same index
        SimpleResponse expectedDuplicate = new SimpleResponse(400, "ERROR: index with <indexName> = 'index_name', already exists.");
        given(ee.createIndex(TEST_INDEX_NAME,"")).willReturn(expectedDuplicate);
        SimpleResponse duplicate = es.createIndex(TEST_INDEX_NAME, "");
        assertEquals(expectedDuplicate, duplicate);
    }

    @Test
    void givenBadIndexParameter_whenIndexDocument_thenReturnError400(){
        String expectedBody = "ERROR: missing required parameter <indexName>";
        SimpleResponse expected = new SimpleResponse(EXPECTED_ERROR_CODE, expectedBody);
        given(ee.indexDocument(null,"2", movie)).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.indexDocument(null, "2", movie);
        assertEquals(given.getBodyMessage(), expectedBody);
        assertEquals(given.getStatusCode(), EXPECTED_ERROR_CODE);
    }

    @Test
    void givenBadMovieParameter_whenIndexDocument_thenReturnError400(){
        String expectedBody = "ERROR: missing required parameter <indexName>";
        SimpleResponse expected = new SimpleResponse(EXPECTED_ERROR_CODE, expectedBody);
        given(ee.indexDocument(TEST_INDEX_NAME,"2", null)).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.indexDocument(TEST_INDEX_NAME, "2", null);
        assertEquals(given.getBodyMessage(), expectedBody);
        assertEquals(given.getStatusCode(), EXPECTED_ERROR_CODE);
    }

    @Test
    void givenValidParameters_whenIndexDocumentWithPostRequest_thenReturnOk(){
        String expectedBody = "* Movie '" + movie.getOriginalTitle() + "', indexed correctly in '" + TEST_INDEX_NAME + "'.";
        SimpleResponse expected = new SimpleResponse(EXPECTED_SUCCESS_CODE, expectedBody);
        given(ee.indexDocument(TEST_INDEX_NAME,null, movie)).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.indexDocument(TEST_INDEX_NAME, null, movie);
        assertEquals(given.getBodyMessage(), expectedBody);
        assertEquals(given.getStatusCode(), EXPECTED_SUCCESS_CODE);
    }

    @Test
    void givenValidParameters_whenIndexDocumentWithPutRequest_thenReturnOk(){
        String id = "t97832451";
        String expectedBody = "* Movie '" + movie.getOriginalTitle() + "', indexed correctly in '" + TEST_INDEX_NAME + "'.";
        SimpleResponse expected = new SimpleResponse(EXPECTED_SUCCESS_CODE, expectedBody);
        given(ee.indexDocument(TEST_INDEX_NAME,id, movie)).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.indexDocument(TEST_INDEX_NAME, id, movie);
        assertEquals(given.getBodyMessage(), expectedBody);
        assertEquals(given.getStatusCode(), EXPECTED_SUCCESS_CODE);
    }
}
