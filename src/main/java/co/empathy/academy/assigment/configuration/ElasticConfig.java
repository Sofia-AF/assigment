package co.empathy.academy.assigment.configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.empathy.academy.assigment.services.*;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticConfig {
    @Bean
    public ElasticEngine elasticEngine() {
        return new ElasticEngineImpl();
    }

    @Bean
    public ElasticService elasticService(ElasticEngine elasticEngine){
        return new ElasticServiceImpl(elasticEngine);
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
