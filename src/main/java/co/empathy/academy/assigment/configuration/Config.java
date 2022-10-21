package co.empathy.academy.assigment.configuration;

import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.empathy.academy.assigment.service.SearchEngine;
import co.empathy.academy.assigment.service.SearchEngineImpl;
import co.empathy.academy.assigment.service.SearchService;
import co.empathy.academy.assigment.service.SearchServiceImpl;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import co.elastic.clients.elasticsearch.ElasticsearchClient;

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

    @Bean
    public ElasticsearchClient getElasticClient(){
        // Create the low-level client
        RestClient httpClient = RestClient.builder(
                new HttpHost("localhost", 9200)
        ).build();

        // Create the Java API Client with the same low level client
        ElasticsearchTransport transport = new RestClientTransport(
                httpClient,
                new JacksonJsonpMapper()
        );
        return new ElasticsearchClient(transport);
    }






}
