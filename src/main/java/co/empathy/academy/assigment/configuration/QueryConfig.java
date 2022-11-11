package co.empathy.academy.assigment.configuration;

import co.empathy.academy.assigment.services.QueryService;
import co.empathy.academy.assigment.services.QueryServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryConfig {
    @Bean
    public QueryService queryService() {
        return new QueryServiceImpl();
    }
}
