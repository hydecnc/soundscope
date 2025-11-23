package org.wavelabs.soundscope.data_access;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wavelabs.soundscope.entity.Song;
import org.wavelabs.soundscope.use_case.fingerprint.FingerprintLookupDAI;

import java.io.IOException;
import java.util.concurrent.*;
import static org.wavelabs.soundscope.data_access.AcousticIDAPIConstants.*;

/**
 * Looks up fingerprints using the AcousticID API, and retrieves corresponding song IDs and metadata
 */
public class AcousticIDFingerprintLookup implements FingerprintLookupDAI {
    private final static String ACOUSTICID_API_KEY = getAPIKey();

    private final static OkHttpClient client = new OkHttpClient();
    private final static BlockingQueue<QueuedRequest> requestQueue = new LinkedBlockingQueue<>();
    private final static ScheduledExecutorService requestScheduler = Executors.newSingleThreadScheduledExecutor();
    private static boolean running = false;


    /**
     * Returns the closest track id to the provided fingerprint.
     * @param fingerprint
     * @param duration
     * @return acoustIDtrackID
     */
    @Override
    public String getClosestMatchID(String fingerprint, int duration) {
        Song.SongMetadata metadata = getClosestMatchMetadata(fingerprint, duration);
        return metadata.acoustIDTrackID();
    }

    /**
     * Returns the metadata of the closest match
     * @param fingerprint
     * @param duration
     * @return songMetadata
     */
    @Override
    public Song.SongMetadata getClosestMatchMetadata(String fingerprint, int duration) {
        CompletableFuture<Response> future = addAPIRequest(fingerprint, duration);

        try {
            Response apiResult = future.get(REQUEST_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            return parseJSONAPIResults(apiResult);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new FingerprintMatchNotFoundException(e.toString());
        }
    }

    /**
     * Parses the JSON results of an API response
     * @param apiResponse
     * @return songMetadata
     */
    private static Song.SongMetadata parseJSONAPIResults(Response apiResponse){
        try{
            JSONObject responseBody = new JSONObject(apiResponse.body().string());

            //Checks response has okay status and non-empty results
            if (!responseBody.getString(STATUS_CODE).equals(SUCCESS_CODE))
                throw new FingerprintMatchNotFoundException(UNSUCCESSFUL_STATUS_MSG);

            //Finds the result with the highest score
            JSONArray results = responseBody.getJSONArray(RESULTS_CODE);
            JSONObject closestResult = results.getJSONObject(0);
            for(int i  = 1; i < results.length(); i++){
                JSONObject currentResult = results.getJSONObject(i);
                if(currentResult.getFloat(MATCH_QUALITY_CODE) > closestResult.getFloat(MATCH_QUALITY_CODE))
                    closestResult = currentResult;
            }

            //Retrieves all relevant Song Metadata attributes from the JSON response
            //Non-critical attributes that cannot be found are set to null
            String acoustIDTrackID = closestResult.getString(ACOUSTICID_TRACK_ID_CODE);

            JSONObject recording = closestResult.getJSONArray(RECORDINGS_CODE).getJSONObject(0);
            int duration = recording.getInt(DURATION_CODE);
            String musicBrainzID = recording.getString(MUSICBRAINZ_ID_CODE);
            String title = recording.getString(SONG_TITLE_CODE);

            String album = recording.getJSONArray(RELEASES_CODE).getJSONObject(0).getString(ALBUM_TITLE_CODE);

            JSONArray artistObjects =  recording.getJSONArray(ARTISTS_CODE);
            String[] artists = new String[artistObjects.length()];
            for(int i  = 0; i < artistObjects.length(); i++){
                artists[i] = artistObjects.getJSONObject(i).getString(ARTIST_NAME_CODE);
            }

            return new Song.SongMetadata(
                title,
                duration,
                musicBrainzID,
                acoustIDTrackID,
                album,
                artists
            );
        }catch (IOException | JSONException e){
            throw new FingerprintMatchNotFoundException(e.toString());
        }
    }

    /**
     * Stores a request and the corresponding future that needs to be completed
     * @param request
     * @param future
     */
    private record QueuedRequest(Request request, CompletableFuture<Response> future) {}

    /**
     * Adds an API request corresponding to the fingerprint and duration to the queue
     * @param fingerprint
     * @param duration
     * @return future
     */
    private synchronized CompletableFuture<Response> addAPIRequest(String fingerprint, int duration){
        String url = ACOUSTICID_API_URL +
                "?client=" + ACOUSTICID_API_KEY +
                "&meta=" + METADATA_REQUEST +
                "&duration=" + duration +
                "&fingerprint=" + fingerprint;

        Request request = new Request.Builder()
                .url(url)
                .build();

        CompletableFuture<Response> future = new CompletableFuture<>();
        requestQueue.add(new QueuedRequest(request, future));

        if (!running) {
            running = true;
            processNextAPIRequest();
        }

        return future;
    }

    /**
     * Processes the next API request in the queue, and completes its corresponding future.
     */
    private static void processNextAPIRequest() {
        QueuedRequest task = requestQueue.poll();

        if(task == null){
            running = false;
            return;
        }

        CompletableFuture<Response> future = task.future;
        Request request = task.request;

        try{
            final Response response = client.newCall(request).execute();
            future.complete(response);
        }catch(IOException event){
            throw new FingerprintMatchNotFoundException(event.toString());
        }

        //Schedules the next API request to be processed after the appropriate spacing
        requestScheduler.schedule(AcousticIDFingerprintLookup::processNextAPIRequest, REQUEST_SPACING_MILLIS, TimeUnit.MILLISECONDS);
    }


    /**
     * Retrieves the API Key from the "ACOUSTICID_API_KEY" environment variable.
     * NOTE: If working with IntelliJ, this must be set in IntelliJ, not through the terminal.
     * @return api_key
     */
    private static String getAPIKey() {
        String api_key = System.getenv("ACOUSTICID_API_KEY");
        if (api_key == null) {
            throw new IllegalStateException("Environment variable ACOUSTICID_API_KEY has not been set");
        }
        return api_key;
    }
}
