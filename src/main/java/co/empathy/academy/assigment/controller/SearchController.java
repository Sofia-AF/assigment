package co.empathy.academy.assigment.controller;

import co.empathy.academy.assigment.model.SimpleResponse;
import co.empathy.academy.assigment.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping("/search")
    public SimpleResponse searchQuery(@RequestParam(value="query") String query) {
        return searchService.searchQuery(query);
    }
}