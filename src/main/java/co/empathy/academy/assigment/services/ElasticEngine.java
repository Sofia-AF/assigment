package co.empathy.academy.assigment.services;

import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.model.SimpleResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ElasticEngine {
    /**
     * Returns the current ElasticSearch version as a String
     * @return : String with current ES version
     * @throws IOException : when can't perform GET request to ElasticSearch
     */
    String getElasticVersion() throws IOException;

    /**
     * Shows all indexes created
     * @return SimpleResponse
     */
    SimpleResponse showAllIndexes();

    /**
     * Creates a new index on <indexName>, with its settings and mappings stored in <body>
     * @param indexName : name to index to create
     * @param body : retrieved String with the settings and
     * @return SimpleResponse
     */
    SimpleResponse createIndex(String indexName, String body);

    /**
     * Indexes a new document in an index. If the target index does not exist, creates new one
     * @param indexName : index target to index the new document
     * @param docId : document identifier (optional)
     * @param movie : request body with info of the movie to index
     * @return SimpleResponse
     */
    SimpleResponse indexDocument(String indexName, String docId, Movie movie);

    /**
     * Bulk index the contents of different files to a new index
     * @param basics : file with basic info to bulk index
     * @param crew : file with crew info to bulk index
     * @param akas : file with akas info to bulk index
     * @param ratings : file with ratings info to bulk index
     * @return SimpleResponse with right status and custom body
     */
    SimpleResponse bulkIndex(MultipartFile basics, MultipartFile crew, MultipartFile akas, MultipartFile ratings,
                             MultipartFile starring);
}
