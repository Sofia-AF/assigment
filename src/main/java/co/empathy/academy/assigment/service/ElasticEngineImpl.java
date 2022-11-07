package co.empathy.academy.assigment.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.model.SimpleResponse;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ElasticEngineImpl implements ElasticEngine {
    private final int SUCCESS_CODE = 200;
    private final int BAD_REQUEST_CODE = 400;
    private final int CLIENT_ERROR_CODE = 403;
    private final int SERVER_ERROR_CODE = 500;
    private final int MAX_LINE_COUNTER = 200;
    private final String IMDB_INDEX = "index";


    @Autowired
    private ElasticsearchClient elastic;

    @Autowired
    private RestClient client;

    /**
     * Returns the current ElasticSearch version as a String
     * @return : String with current ES version
     * @throws IOException : when can't perform GET request to ElasticSearch
     */
    @Override
    public String getElasticVersion() throws IOException {
        // Creates GET request to elasticSearch
        Response response = client.performRequest(new Request("GET","/"));
        // Retrieves info from cluster
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject json = new JSONObject(responseBody);
        JSONObject version = json.getJSONObject("version");
        // Gets current version from JSON response
        String numberVersion = version.getString("number");

        return numberVersion;
    }

    /**
     * Shows all indexes created
     * @return : SimpleResponse where bodyMessage is a string with all the indexes found
     */
    @Override
    public SimpleResponse showAllIndexes() {
        String responseBody, endPoint = "/_cat/indices/";
        try {
            // Creates GET request to endPoint
            Response response = client.performRequest(new Request("GET", endPoint));
            responseBody = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            return new SimpleResponse(SERVER_ERROR_CODE, "ERROR: can't connect to server.");
        }
        return new SimpleResponse(SUCCESS_CODE, responseBody);
    }

    /**
     * Creates a new index on <indexName>, with its settings and mappings stored in <body>
     * @param indexName : name to index to create
     * @param body      : retrieved String with the settings and
     * @return SimpleResponse
     */
    @Override
    public SimpleResponse createIndex(String indexName, String body) {
        // First checks parameters
        if(indexName == null)
            return new SimpleResponse(BAD_REQUEST_CODE, "ERROR: missing required parameter <indexName>");
        else if (body == null)
            return new SimpleResponse(BAD_REQUEST_CODE, "ERROR: missing JSON body in PUT request.");

        // Opens input stream for the string body
        InputStream inputBody = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));

        // Fills index request with the contents for the new indexName
        CreateIndexRequest request = CreateIndexRequest.of(b -> b
                .index(indexName).withJson(inputBody));

        try {
            // Creates the index, and checks it's been created correctly
            if(elastic.indices().create(request).acknowledged())
                return new SimpleResponse(SUCCESS_CODE, " * Index '"+indexName+"' created correctly.");
        } catch (IOException e) {
            return new SimpleResponse(SERVER_ERROR_CODE, "ERROR: can't connect to server.");
        } catch (Exception e){
            return new SimpleResponse(BAD_REQUEST_CODE, "ERROR: index with <indexName> = '"+indexName+"', already exists.");
        }
        // Can't create index for unhandled reasons
        return new SimpleResponse(BAD_REQUEST_CODE, "ERROR: can't index '"+indexName+"'.");
    }

    /**
     * Indexes a new document in an index. If the target index does not exist, creates new one
     * @param indexName : index target to index the new document
     * @param docId : document identifier (optional)
     * @param movie : request body with info of the movie to index
     * @return SimpleResponse
     */
    @Override
    public SimpleResponse indexDocument(String indexName, String docId, Movie movie) {
        // First checks parameters
        if(indexName == null)
            return new SimpleResponse(BAD_REQUEST_CODE, "ERROR: missing required parameter <indexName>");
        else if (movie == null)
            return new SimpleResponse(BAD_REQUEST_CODE, "ERROR: missing JSON body in request.");

        try {
            // Checks type of request
            if(docId == null)
                // Post request
                elastic.index(i -> i.index(indexName).document(movie));
            else
                // Put request
                elastic.index(i -> i.index(indexName).id(docId).document(movie));
            return new SimpleResponse(SUCCESS_CODE, "* Movie '" + movie.getOriginalTitle() + "', indexed correctly in '" + indexName + "'.");
        } catch (IOException e) {
            return new SimpleResponse(SERVER_ERROR_CODE, "ERROR: can't connect to server.");
        } catch (Exception e){
            // Can't index document for unhandled reasons
            return new SimpleResponse(CLIENT_ERROR_CODE, "ERROR: can't index document in index '"+indexName+"'.");
        }

    }

    @Override
    public SimpleResponse searchIndex(String indexName, String body){
        // First checks parameters
        if(indexName == null)
            return new SimpleResponse(BAD_REQUEST_CODE, "ERROR: missing required parameter <indexName>");
        else if (body == null)
            return new SimpleResponse(BAD_REQUEST_CODE, "ERROR: missing JSON body in PUT request.");


        JSONObject query = new JSONObject(body).getJSONObject("query");
        Iterator<String> keys = query.keys();
        String queryType = query.keys().next();
        JSONObject typeJSON = query.getJSONObject(queryType);

        if(queryType.equals("multi_match"))
            return makeMultiMatchQuery(indexName, typeJSON);
        else if(queryType.equals("term"))
            return makeTermQuery(indexName, keys);
        else if(queryType.contains("terms"))
            return makeTermsQuery(indexName, keys);
        else
            return new SimpleResponse(BAD_REQUEST_CODE, "ERROR: query search not allowed");

        /**
        while(keys.hasNext()) {
            String key = keys.next();
            if (posts.get(key) instanceof JSONObject) {
                // do something with jsonObject here
                System.out.println(" * "+posts.get(key));
            }
        }


         * JSONObject type = posts.getJSONObject("multi_match");
         *         Set<String> keys2 = type.keySet();
         *         for(String s : keys2)
         *             System.out.println(s);



        // return new SimpleResponse(200, responseBody);
        return new SimpleResponse(0, null);
         */
    }

    private SimpleResponse makeMultiMatchQuery(String indexName, JSONObject typeJSON) {
        String query = typeJSON.getString("query");
        List<String> fields = new ArrayList<>();
        for(Object field : typeJSON.getJSONArray("fields"))
            fields.add(field.toString());

        try {
            MultiMatchQuery mm = MultiMatchQuery.of(m -> m.query(query).fields(fields));
            SearchResponse<Movie> response = elastic.search(s -> s.index(indexName).query(mm._toQuery()), Movie.class);
            List<Hit<Movie>> hits = response.hits().hits();
            for (Hit<Movie> hit: hits) {
                Movie movieFound = hit.source();
                System.out.println(" * Found movie '" + movieFound.getOriginalTitle() + "', with score = " + hit.score());
            }
            return new SimpleResponse(SUCCESS_CODE, response.toString());
        } catch (IOException e) {
            return new SimpleResponse(SERVER_ERROR_CODE, "ERROR: can't connect to server.");
        }
    }

    public SimpleResponse makeTermQuery(String indexName, Iterator<String> keys){
        System.out.println("Term");
        return null;
    }

    public SimpleResponse makeTermsQuery(String indexName, Iterator<String> keys){
        System.out.println("Terms");
        return null;
    }


    /**
     * Bulk index the parsed contents of a file to a new index
     * @param multipartFile : file with contents to index
     * @return SimpleResponse
     */
    @Override
    public SimpleResponse bulkIndex(MultipartFile multipartFile){
        if(multipartFile.isEmpty())
            return new SimpleResponse(SUCCESS_CODE, "Nothing to index.");
        List<Movie> movies = new ArrayList<Movie>();
        InputStream stream = null;

        try {
            stream = multipartFile.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            bufferedReader.readLine();
            int lineCounter = 0;
            String newMovie;
            while((newMovie = bufferedReader.readLine()) != null){
                addMovie(newMovie, movies);
                lineCounter++;
                if(lineCounter == MAX_LINE_COUNTER){
                    indexNewBulk(movies);
                    movies = new ArrayList<Movie>();
                    lineCounter = 0;
                }
            }
            return new SimpleResponse(SUCCESS_CODE, "All movies from '"+multipartFile.getOriginalFilename()+
                    "' were successfully indexed into '"+IMDB_INDEX+"' index.");
        } catch (IOException e) {
            return new SimpleResponse(BAD_REQUEST_CODE, "ERROR while indexing file '"+multipartFile.getOriginalFilename()+"'");
        }
    }

    /**
     * Bulk index a list of movies
     * @param movies : current bulk list
     */
    public void indexNewBulk(List<Movie> movies) throws IOException {
        BulkRequest.Builder br = new BulkRequest.Builder();

        for (Movie movie : movies) {
            br.operations(op -> op
                    .index(idx -> idx
                            .index(IMDB_INDEX)
                            .id(movie.getTconst())
                            .document(movie)
                    )
            );
        }

        BulkResponse result = elastic.bulk(br.build());
    }

    /**
     * Parse a line with Movie.class parameters. Adds the created movie to the current bulk list
     * @param line : current line from the file that we're reading
     * @param movies : current bulk list
     */
    private void addMovie(String line, List<Movie> movies) {
        String[] token = line.split("\t");
        Movie newMovie = new Movie(token[0], token[1], token[2], token[3],
                token[4].equals("0") ? true : false,
                token[5].equals("\\N") ? 0 : Integer.parseInt(token[5]),
                token[6].equals("\\N") ? 0 : Integer.parseInt(token[6]),
                token[7].equals("\\N") ? 0 : Integer.parseInt(token[7]),
                token[8]);
        movies.add(newMovie);
    }

}
