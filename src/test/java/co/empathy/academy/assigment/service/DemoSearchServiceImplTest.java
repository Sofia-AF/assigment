package co.empathy.academy.assigment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DemoSearchServiceImplTest {

    @Test
    void givenQueryWithResults_whenSearch_thenReturnNonZeroNumFound() {
        String query = "query with results";
        SearchEngine searchEngine = mock(SearchEngine.class);
        given(searchEngine.search(query)).willReturn(5);

        SearchService searchService = new SearchServiceImpl(searchEngine);

        int numResults = searchService.search(query);

        assertTrue(numResults > 0);
    }

    @Test
    void givenQueryWithNoResults_whenSearch_thenReturnZeroNumFound() {
        String query = "query with no results";
        SearchEngine searchEngine = mock(SearchEngine.class);
        given(searchEngine.search(query)).willReturn(0);

        SearchService searchService = new SearchServiceImpl(searchEngine);

        int numResults = searchService.search(query);

        assertEquals(0, numResults);
    }

    @Test
    void givenNoQuery_whenSearch_thenPropagateError() {
        SearchEngine searchEngine = mock(SearchEngine.class);
        Throwable expectedException = new RuntimeException("Error while searching");
        given(searchEngine.search(null)).willThrow(expectedException);

        SearchService searchService = new SearchServiceImpl(searchEngine);

        assertThrows(expectedException.getClass(), () -> searchService.search(null));
    }

}
