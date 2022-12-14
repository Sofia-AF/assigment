package co.empathy.academy.assigment.configuration;

import co.empathy.academy.assigment.services.SearchEngine;
import co.empathy.academy.assigment.services.SearchEngineImpl;
import co.empathy.academy.assigment.services.SearchService;
import co.empathy.academy.assigment.services.SearchServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// This is the configuration class where we'll define our beans (objects whose lifecycle is managed by Spring)
@Configuration
public class Config {

    @Bean
    public SearchEngine searchEngine() {
        return new SearchEngineImpl();
    }

    @Bean
    public SearchService searchService(SearchEngine searchEngine) {
        return new SearchServiceImpl(searchEngine);
    }

}
