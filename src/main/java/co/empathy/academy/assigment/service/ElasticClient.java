package co.empathy.academy.assigment.service;

import co.empathy.academy.assigment.model.Movie;

import java.io.IOException;

public interface ElasticClient {
    /**
     * Returns the current ElasticSearch version as a String
     * @return : String with current ES version
     * @throws IOException : when can't perform GET request to ElasticSearch
     */
    String getElasticVersion() throws IOException;

    /**
     * Shows all indexes created
     * @return : String with all indexes found
     * @throws IOException : when can't perform GET request to ElasticSearch
     */
    String showAllIndexes() throws IOException;

    /**
     * Creates a new index on <indexName>, with its settings and mappings stored in <body>
     * @param indexName : name to index to create
     * @param body : retrieved String with the settings and
     * @throws IOException : when can't perform index creation request to ElasticSearch
     */
    void createIndex(String indexName, String body) throws IOException;

    /**
     * Indexes a new document in a index. If the target index does not exist, creates new one
     * @param indexName : index target to index the new document
     * @param docId : document identifier (optional)
     * @param movie : request body with info of the movie to index
     * @throws IOException : when can't perform indexing request to ElasticSearch
     */
    void indexDocument(String indexName, String docId, Movie movie) throws IOException;

}
