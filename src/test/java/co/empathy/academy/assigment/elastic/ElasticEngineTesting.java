package co.empathy.academy.assigment.elastic;

import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.model.SimpleResponse;
import co.empathy.academy.assigment.service.ElasticEngine;
import co.empathy.academy.assigment.service.ElasticEngineImpl;
import co.empathy.academy.assigment.service.ElasticService;
import co.empathy.academy.assigment.service.ElasticServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ElasticEngineTesting {

    @Test
    void givenValidRequest_whenShowAllResults_thenReturnOk(){
        ElasticEngine ee = mock(ElasticEngineImpl.class);
        int expectedCode = 200;
        String expectedBody = "green  open .geoip_databases _zvzsizZQ9iChtYCVp5vjw 1 0 40 40 38.3mb 38.3mb";
        SimpleResponse expected = new SimpleResponse(expectedCode, expectedBody);
        given(ee.showAllIndexes()).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.showAllIndexes();
        assertEquals(given.getBodyMessage(), expectedBody);
        assertEquals(given.getStatusCode(), expectedCode);
    }

    @Test
    void givenCorrectIndexParameters_whenCreateIndex_thenReturnOk(){
        ElasticEngine ee = mock(ElasticEngineImpl.class);
        int expectedCode = 200;
        String expectedBody = "* Index 'index_test' created correctly.";
        String indexName = "index_test";
        SimpleResponse expected = new SimpleResponse(expectedCode, expectedBody);
        given(ee.createIndex(indexName,"")).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.createIndex(indexName, "");
        assertEquals(given.getBodyMessage(), expectedBody);
        assertEquals(given.getStatusCode(), expectedCode);
    }

    @Test
    void givenBadIndexParameter_whenCreateIndex_thenReturnError400(){
        ElasticEngine ee = mock(ElasticEngineImpl.class);
        int expectedCode = 400;
        String expectedBody = "ERROR: missing required parameter <indexName>";
        SimpleResponse expected = new SimpleResponse(expectedCode, expectedBody);
        given(ee.createIndex(null,"")).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.createIndex(null, "");
        assertEquals(given.getBodyMessage(), expectedBody);
        assertEquals(given.getStatusCode(), expectedCode);
    }

    @Test
    void givenBadBodyParameter_whenCreateIndex_thenReturnError400(){
        ElasticEngine ee = mock(ElasticEngineImpl.class);
        int expectedCode = 400;
        String expectedBody = "ERROR: missing JSON body in PUT request.";
        SimpleResponse expected = new SimpleResponse(expectedCode, expectedBody);
        given(ee.createIndex("index_name",null)).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.createIndex("index_name", null);
        assertEquals(given.getBodyMessage(), expectedBody);
        assertEquals(given.getStatusCode(), expectedCode);
    }

    @Test
    void givenSameIndexDuplicate_whenCreateIndex_thenReturnError(){
        // First create the new index
        ElasticEngine ee = mock(ElasticEngineImpl.class);
        int expectedCode = 200;
        String expectedBody = "* Index 'index_test' created correctly.";
        String indexName = "index_test";
        SimpleResponse expected = new SimpleResponse(expectedCode, expectedBody);
        given(ee.createIndex(indexName,"")).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.createIndex(indexName, "");

        // Then index the same index
        SimpleResponse expectedDuplicate = new SimpleResponse(400, "ERROR: index with <indexName> = 'index_name', already exists.");
        given(ee.createIndex(indexName,"")).willReturn(expectedDuplicate);
        SimpleResponse duplicate = es.createIndex(indexName, "");
        assertEquals(expectedDuplicate, duplicate);
    }

    @Test
    void givenBadIndexParameter_whenIndexDocument_thenReturnError400(){
        ElasticEngine ee = mock(ElasticEngineImpl.class);
        Movie movie = new Movie("Cars", 2006, "animation", "someone");
        int expectedCode = 400;
        String expectedBody = "ERROR: missing required parameter <indexName>";
        SimpleResponse expected = new SimpleResponse(expectedCode, expectedBody);
        given(ee.indexDocument(null,"2", movie)).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.indexDocument(null, "2", movie);
        assertEquals(given.getBodyMessage(), expectedBody);
        assertEquals(given.getStatusCode(), expectedCode);
    }

    @Test
    void givenBadMovieParameter_whenIndexDocument_thenReturnError400(){
        ElasticEngine ee = mock(ElasticEngineImpl.class);
        int expectedCode = 400;
        String expectedBody = "ERROR: missing required parameter <indexName>";
        SimpleResponse expected = new SimpleResponse(expectedCode, expectedBody);
        given(ee.indexDocument("name","2", null)).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.indexDocument("name", "2", null);
        assertEquals(given.getBodyMessage(), expectedBody);
        assertEquals(given.getStatusCode(), expectedCode);
    }

    @Test
    void givenValidParameters_whenIndexDocumentWithPostRequest_thenReturnOk(){
        ElasticEngine ee = mock(ElasticEngineImpl.class);
        Movie movie = new Movie("Cars", 2006, "animation", "someone");
        int expectedCode = 400;
        String indexName = "name";
        String expectedBody = "* Movie '" + movie.getTitle() + "', indexed correctly in '" + indexName + "'.";
        SimpleResponse expected = new SimpleResponse(expectedCode, expectedBody);
        given(ee.indexDocument("name",null, movie)).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.indexDocument("name", null, movie);
        assertEquals(given.getBodyMessage(), expectedBody);
        assertEquals(given.getStatusCode(), expectedCode);
    }

    @Test
    void givenValidParameters_whenIndexDocumentWithPutRequest_thenReturnOk(){
        ElasticEngine ee = mock(ElasticEngineImpl.class);
        Movie movie = new Movie("Cars", 2006, "animation", "someone");
        int expectedCode = 400;
        String id = "t97832451";
        String indexName = "name";
        String expectedBody = "* Movie '" + movie.getTitle() + "', indexed correctly in '" + indexName + "'.";
        SimpleResponse expected = new SimpleResponse(expectedCode, expectedBody);
        given(ee.indexDocument("name",id, movie)).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.indexDocument("name", id, movie);
        assertEquals(given.getBodyMessage(), expectedBody);
        assertEquals(given.getStatusCode(), expectedCode);
    }
}
