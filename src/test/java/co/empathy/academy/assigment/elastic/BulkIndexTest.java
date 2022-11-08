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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class BulkIndexTest {
    private final ElasticEngine ee = mock(ElasticEngineImpl.class);
    private final int EXPECTED_SUCCESS_CODE = 200;
    private final int EXPECTED_ERROR_CODE = 400;
    private final Movie movie1 = new Movie("id1","Movie", "Cars", "Cars", false, 2006, 0, 120, "animation");
    private final MultipartFile validFile = new MockMultipartFile("validFile", "validFile", "text/plain", movie1.toString().getBytes());
    private final MultipartFile emptyFile = new MockMultipartFile("emptyFile", "emptyFile", "text/plain", "".getBytes());

    @Test
    public void givenNothing_whenBulkIndexing_thenReturnError(){
        SimpleResponse expected = new SimpleResponse(EXPECTED_ERROR_CODE, "ERROR: file's not valid.");
        given(ee.bulkIndex(null)).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.bulkIndex(null);
        assertEquals(expected, given);
    }

    @Test
    public void givenEmptyFile_whenBulkIndex_thenReturnOk(){
        SimpleResponse expected = new SimpleResponse(EXPECTED_SUCCESS_CODE, "Nothing to index.");
        given(ee.bulkIndex(emptyFile)).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.bulkIndex(emptyFile);
        assertEquals(expected, given);
    }

    @Test
    public void givenValidFile_whenBulkIndex_thenReturnOk(){
        SimpleResponse expected = new SimpleResponse(EXPECTED_SUCCESS_CODE, "All movies from '"+ validFile.getOriginalFilename()+
                "' were successfully indexed into 'imdb' index.");
        given(ee.bulkIndex(validFile)).willReturn(expected);
        ElasticService es = new ElasticServiceImpl(ee);
        SimpleResponse given = es.bulkIndex(validFile);
        System.out.println(given.getBodyMessage());
        assertEquals(given, expected);
    }




}
