package co.empathy.academy.assigment.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.empathy.academy.assigment.model.Aka;
import co.empathy.academy.assigment.model.Movie;
import co.empathy.academy.assigment.model.Director;
import co.empathy.academy.assigment.model.SimpleResponse;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ElasticEngineImpl implements ElasticEngine {
    private final int SUCCESS_CODE = 200;
    private final int BAD_REQUEST_CODE = 400;
    private final int CLIENT_ERROR_CODE = 403;
    private final int SERVER_ERROR_CODE = 500;
    private final int MAX_LINE_COUNTER = 200000;
    private final String IMDB_INDEX = "imdb";
    private final int TOP_MAX_COUNT = 50;


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
        if (body == null) {
            return new SimpleResponse(BAD_REQUEST_CODE, "ERROR: missing JSON body in PUT request.");
        }
        // Creates index request with default settings
        InputStream mappings = getClass().getClassLoader().getResourceAsStream("default-mappings.json");

        // Opens input stream for the request body
        //InputStream mappings = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
        // Fills index request with the contents for the new indexName
        CreateIndexRequest request = CreateIndexRequest.of(b -> b.index(indexName).withJson(mappings));

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
        if (movie == null)
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

    /**
     * Function to check some files and perform the request to Elastic
     * @param basics : file with basic info to bulk index
     * @param crew : file with crew info to bulk index
     * @param akas : file with akas info to bulk index
     * @param ratings : file with ratings info to bulk index
     * @return SimpleResponse with right status and custom body
     */
    @Override
    public SimpleResponse bulkIndex(MultipartFile basics, MultipartFile crew, MultipartFile akas,
                                    MultipartFile ratings){
        if(basics.isEmpty())
            return new SimpleResponse(SUCCESS_CODE, "Nothing to index.");
        List<Movie> movies = new ArrayList<Movie>();
        InputStream stream = null;

        try {
            // Check if there's already an index with name IMDB_INDEX
            Response response = client.performRequest(new Request("HEAD", "/" + IMDB_INDEX));
            if (response.getStatusLine().getStatusCode() == 200){
                // If there's an index with that name, we delete it
                elastic.indices().delete(d -> d.index(IMDB_INDEX));
            }
            // We create the IMDB index, with default configuration and mappings
            createIndex(IMDB_INDEX, "");

            // Read all docs given, and indexing all the contents
            indexAllDocsImdb(basics, crew, akas, ratings);
            return new SimpleResponse(SUCCESS_CODE, "All movies from '"+basics.getOriginalFilename()+
                    "' were successfully indexed into '"+IMDB_INDEX+"' index.");
        } catch (IOException e) {
            return new SimpleResponse(BAD_REQUEST_CODE, "ERROR while indexing file '"+basics.getOriginalFilename()+"'");
        }
    }

    /**
     * Auxiliar function to bulk index the contents of different files to a new index
     * @param basicsFile : file with basic info to bulk index
     * @param crewFile : file with crew info to bulk index
     * @param akasFile : file with akas info to bulk index
     * @param ratingsFile : file with ratings info to bulk index
     * @return SimpleResponse with right status and custom body
     */
    public void indexAllDocsImdb(MultipartFile basicsFile, MultipartFile crewFile, MultipartFile akasFile,
                                 MultipartFile ratingsFile){
        List<Movie> movies = new ArrayList<Movie>();
        try {
            // First we initialize our BufferReaders
            BufferedReader basics = new BufferedReader(new InputStreamReader(basicsFile.getInputStream(), StandardCharsets.UTF_8));
            BufferedReader crew = new BufferedReader(new InputStreamReader(crewFile.getInputStream(), StandardCharsets.UTF_8));
            BufferedReader akas = new BufferedReader(new InputStreamReader(akasFile.getInputStream(), StandardCharsets.UTF_8));
            BufferedReader ratings = new BufferedReader(new InputStreamReader(ratingsFile.getInputStream(), StandardCharsets.UTF_8));

            // And skip the first lines with the headers
            basics.readLine();
            crew.readLine();
            akas.readLine();
            ratings.readLine();

            int lineCounter = 0;
            String currentMovie;
            while((currentMovie = basics.readLine()) != null){
                addMovie(currentMovie, akas, crew, ratings, movies);
                lineCounter++;
                // Index bulks of MAX_LINE_COUNTER movies
                if(lineCounter == MAX_LINE_COUNTER){
                    indexNewBulk(movies);
                    movies = new ArrayList<Movie>();
                    lineCounter = 0;
                }
            }
            // Index the last part of the bulk
            indexNewBulk(movies);

            // Close streams
            basics.close();
            crew.close();
            akas.close();
            ratings.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks crew for movie with id = movieId, and returns a lists with crew for that specific movie
     * @param movieId : movie we want to check
     * @param crewLine : BufferedReader for title.crew file
     * @return : List of crew of that specific movie
     */
    public List<Director> readCrew(String movieId, BufferedReader crewLine){
        String line;
        String[] token, stringDirectors;
        List<Director> directors = new ArrayList<>();
        int maxCount = 0; // counts max number of tries to readLine to check movieId
        try {
            crewLine.mark(2000);
            while(maxCount < TOP_MAX_COUNT) {
                if((line = crewLine.readLine()) == null){
                    maxCount++;
                }else{
                    token = line.split("\t");
                    if (token[0].equals(movieId)){
                        stringDirectors = token[1].split(",");
                        for(String d : stringDirectors)
                            directors.add(new Director(d));
                        return directors;
                    }
                    else
                        maxCount++;
                }
            }
            crewLine.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Checks akas for movie with id = movieId
     * @param movieId : movie we want to check
     * @param akasLine : BufferedReader for title.akas file
     * @return List of akas of that specific movie
     */
    public List<Aka> readAkas(String movieId, BufferedReader akasLine){
        List<Aka> list = new ArrayList<>();
        int maxCount = 0; // counts max number of tries to readLine to check movieId
        boolean found = false;  // checks if current movieId was found
        try {
            while(maxCount < TOP_MAX_COUNT){
                akasLine.mark(1000);
                String[] token = akasLine.readLine().split("\t");
                if(token[0].equals(movieId)){
                    found = true;
                    list.add(new Aka(token[2], token[3], token[4],
                            token[7].equals("1")));
                } else{
                    maxCount++;
                    akasLine.reset();
                    if(found)
                        // break while condition
                        maxCount += TOP_MAX_COUNT;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * Checks ratings and number of votes for movie with id = movieId, and returns a lists with ratings for that
     * specific movie
     * @param movieId : movie we want to check
     * @param ratingsLine : BufferedReader for title.ratings file
     * @return : List of ratings of that specific movie or null if it's a TV series (they have no ratings)
     */
    public String[] readRatings(String movieId, BufferedReader ratingsLine){
        String line;
        String[] token;
        int maxCount = 0; // counts max number of tries to readLine to check movieId
        try {
            ratingsLine.mark(1000);
            while(maxCount < TOP_MAX_COUNT) {
                if((line = ratingsLine.readLine()) == null){
                    maxCount++;
                }else{
                    token = line.split("\t");
                    if (token[0].equals(movieId)){
                        return token;
                    }
                    else
                        maxCount++;
                }
            }
            ratingsLine.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
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
     *
     * @param line: current line from the file that we're reading
     * @param akas : BufferedReader for file title.akas
     * @param crew : BufferedReader for file title.crew
     * @param ratingsLine : BufferedReader for file title.ratings
     * @param movies: current bulk list
     */
    private void addMovie(String line, BufferedReader akas, BufferedReader crew,
                          BufferedReader ratingsLine, List<Movie> movies) {
        // We split the current line, so we can insert the different params of a movie
        String[] token = line.split("\t");
        String movieId = token[0];
        String[] ratings = readRatings(movieId, ratingsLine);
        float avg;  // average rating of a movie
        int votes;  // number of votes to determine a movie's rating
        // Check if it's a movie, if not, default values of avg and votes are 0
        if(ratings == null){
            avg = 0f; votes = 0;
        }else{
            avg = Float.parseFloat(ratings[1]);
            votes = Integer.parseInt(ratings[2]);
        }
        // We add to our movie list bulk a new movie read in the current line
        Movie currentMovie = new Movie(movieId, token[1], token[2], token[3],
                token[4].equals("0") ? true : false,
                token[5].equals("\\N") ? 0 : Integer.parseInt(token[5]),
                token[6].equals("\\N") ? 0 : Integer.parseInt(token[6]),
                token[7].equals("\\N") ? 0 : Integer.parseInt(token[7]),
                // Genres are typed as String[], so we split them as a list
                Arrays.asList(token[8].split(",")),
                avg, votes,
                readAkas(movieId, akas),
                readCrew(movieId, crew));

        // We don't want to index adult movies
        if(!currentMovie.getIsAdult())
            movies.add(currentMovie);
    }

}
