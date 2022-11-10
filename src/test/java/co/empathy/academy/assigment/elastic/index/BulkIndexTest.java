package co.empathy.academy.assigment.elastic.index;

import co.empathy.academy.assigment.model.Aka;
import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.model.Principal;
import co.empathy.academy.assigment.model.SimpleResponse;
import co.empathy.academy.assigment.services.ElasticEngine;
import co.empathy.academy.assigment.services.ElasticEngineImpl;
import co.empathy.academy.assigment.services.ElasticService;
import co.empathy.academy.assigment.services.ElasticServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class BulkIndexTest {
    private final ElasticEngine ee = mock(ElasticEngineImpl.class);
    private final int EXPECTED_SUCCESS_CODE = 200;
    private final int EXPECTED_ERROR_CODE = 400;
    private final List<Aka> akas = new ArrayList<>();
    private final List<Principal> principals = new ArrayList<>(){{
        add(new Principal("p1"));
    }};
    private final List<String> genres = new ArrayList<>(){{
        add("Animation");
    }};
    private final Movie movie1 = new Movie("id1","Movie", "Cars", "Cars", false, 2006, 0, 120, genres, 7.0f, 100, akas, principals);
    private final MultipartFile basic = new MockMultipartFile("validFile", "validFile", "text/plain", movie1.toString().getBytes());
    private final MultipartFile principal = new MockMultipartFile("principal", "principal", "text/plain", principals.get(0).toString().getBytes());
    private final MultipartFile aka = new MockMultipartFile("aka", "aka", "text/plain", "Coches".getBytes());

    private final MultipartFile ratings = new MockMultipartFile("ratings", "ratings", "text/plain", "ratings".getBytes());


    @Test
    public void givenNoBasicFile_whenBulkIndexing_thenReturnError(){
        SimpleResponse expected = new SimpleResponse(EXPECTED_ERROR_CODE, "ERROR: file's not valid.");
        given(ee.bulkIndex(null, principal, aka, ratings)).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.bulkIndex(null, principal, aka, ratings);
        assertEquals(expected, given);
    }

    @Test
    public void givenNoPrincipalsFile_whenBulkIndexing_thenReturnError(){
        SimpleResponse expected = new SimpleResponse(EXPECTED_ERROR_CODE, "ERROR: file's not valid.");
        given(ee.bulkIndex(basic, null, aka, ratings)).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.bulkIndex(basic, null, aka, ratings);
        assertEquals(expected, given);
    }

    @Test
    public void givenValidFile_whenBulkIndex_thenReturnOk(){
        SimpleResponse expected = new SimpleResponse(EXPECTED_SUCCESS_CODE, "All movies from '"+ basic.getOriginalFilename()+
                "' were successfully indexed into 'imdb' index.");
        given(ee.bulkIndex(basic, principal, aka, ratings)).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.bulkIndex(basic, principal, aka, ratings);
        assertEquals(given, expected);
    }




}
