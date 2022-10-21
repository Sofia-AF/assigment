package co.empathy.academy.assigment.service;

import co.empathy.academy.assigment.model.SimpleResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class SearchServiceImplTest {

    @Test
    void givenValidQuery_whenSearch_thenReturnBasicResponseFound(){
        String query = "shirt";
        String version = "7.17.2";
        SearchEngine searchEngine = mock(SearchEngine.class);
        SimpleResponse expectedResponse = new SimpleResponse(query, version);
        given(searchEngine.searchQuery(query)).willReturn(expectedResponse);
        SearchService searchService = new SearchServiceImpl(searchEngine);
        SimpleResponse givenResponse = searchService.searchQuery(query);
        assertEquals(expectedResponse, givenResponse);
    }

    @Test
    void givenNotValidQuery_whenSearch_thenReturnError(){
        SearchEngine searchEngine = mock(SearchEngine.class);
        Throwable expectedException = new RuntimeException("Error while searching");
        given(searchEngine.searchQuery(null)).willThrow(expectedException);
        SearchService searchService = new SearchServiceImpl(searchEngine);
        assertThrows(expectedException.getClass(), () -> searchService.searchQuery(null));
    }

}
