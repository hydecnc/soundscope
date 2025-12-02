package org.wavelabs.soundscope.data_access;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wavelabs.soundscope.entity.Song;
import org.wavelabs.soundscope.use_case.identify.IdentifyDAI;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Looks up fingerprints using the AcousticID API, and retrieves corresponding song IDs and metadata.
 */
public final class AcoustIDIdentify implements IdentifyDAI {
    private static AcoustIDIdentify instance;

    private final String acoustIDApiKey;
    private final OkHttpClient client;
    private final BlockingQueue<QueuedRequest> requestQueue;
    private final ScheduledExecutorService requestScheduler;
    private boolean running;

    /**
     * Initializes Identifier.
     */
    private AcoustIDIdentify() {
        acoustIDApiKey = getAPIKey();
        client = new OkHttpClient();
        requestQueue = new LinkedBlockingQueue<>();
        requestScheduler = Executors.newSingleThreadScheduledExecutor();
        running = false;
    }

    /**
     * Ensures there's always only one class instance.
     * @return An instance of AcousticIDIdentify class
     */
    public static AcoustIDIdentify getAcoustIDIdentify() {
        if (instance == null) {
            instance = new AcoustIDIdentify();
        }
        return instance;
    }

    /**
     * Parses the JSON results of an API response.
     *
     * @param apiResponse response from API
     * @return songMetadata a Song.SongMetadata object
     * @throws FingerprintMatchNotFoundException when a fingerprint isn't found
     */
    private Song.SongMetadata parseJSONApiResults(Response apiResponse) {
        try {
            final JSONObject responseBody = new JSONObject(apiResponse.body().string());

            // Checks response has okay status and non-empty results
            if (!responseBody.getString(AcoustIDApiConstants.STATUS_CODE)
                    .equals(AcoustIDApiConstants.SUCCESS_CODE)) {
                throw new FingerprintMatchNotFoundException(
                        AcoustIDApiConstants.UNSUCCESSFUL_STATUS_MSG);
            }

            // Finds the result with the highest score
            final JSONArray results = responseBody.getJSONArray(AcoustIDApiConstants.RESULTS_CODE);
            JSONObject closestResult = results.getJSONObject(0);
            for (int i = 1; i < results.length(); i++) {
                final JSONObject currentResult = results.getJSONObject(i);
                if (currentResult.getFloat(AcoustIDApiConstants.MATCH_QUALITY_CODE)
                        > closestResult.getFloat(AcoustIDApiConstants.MATCH_QUALITY_CODE)) {
                    closestResult = currentResult;
                }
            }

            // Retrieves all relevant Song Metadata attributes from the JSON response
            // Non-critical attributes that cannot be found are set to null
            final String acoustIDTrackID = closestResult.getString(
                    AcoustIDApiConstants.ACOUST_ID_TRACK_ID_CODE);

            final JSONObject recording = closestResult.getJSONArray(
                    AcoustIDApiConstants.RECORDINGS_CODE).getJSONObject(0);
            final String musicBrainzID = recording.getString(
                    AcoustIDApiConstants.MUSICBRAINZ_ID_CODE);
            final String title = recording.getString(AcoustIDApiConstants.SONG_TITLE_CODE);

            final String album = recording.getJSONArray(AcoustIDApiConstants.RELEASES_CODE)
                    .getJSONObject(0).getString(AcoustIDApiConstants.ALBUM_TITLE_CODE);

            final JSONArray artistObjects = recording.getJSONArray(AcoustIDApiConstants.ARTISTS_CODE);
            final String[] artists = new String[artistObjects.length()];
            for (int i = 0; i < artistObjects.length(); i++) {
                artists[i] = artistObjects.getJSONObject(i).getString(AcoustIDApiConstants.ARTIST_NAME_CODE);
            }

            return new Song.SongMetadata(
                title,
                musicBrainzID,
                acoustIDTrackID,
                album,
                artists
            );
        }
        catch (IOException | JSONException exception) {
            throw new FingerprintMatchNotFoundException(exception.toString());
        }
    }

    /**
     * Processes the next API request in the queue, and completes its corresponding future.
     *
     * @throws FingerprintMatchNotFoundException when no fingerprint found
     */
    private void processNextAPIRequest() {
        final QueuedRequest task = requestQueue.poll();

        if (task == null) {
            running = false;
            return;
        }

        final CompletableFuture<Response> future = task.future;
        final Request request = task.request;

        try {
            final Response response = client.newCall(request).execute();
            future.complete(response);
        }
        catch (IOException event) {
            throw new FingerprintMatchNotFoundException(event.toString());
        }

        // Schedules the next API request to be processed after the appropriate spacing
        requestScheduler.schedule(this::processNextAPIRequest,
                AcoustIDApiConstants.REQUEST_SPACING_MILLIS, TimeUnit.MILLISECONDS);
    }

    /**
     * Retrieves the API Key from the "ACOUSTICID_API_KEY" environment variable.
     * NOTE: If working with IntelliJ, this must be set in IntelliJ, not through the terminal.
     *
     * @return api_key
     *
     * @throws IllegalStateException for when there is no ENV key set
     */
    private static String getAPIKey() {
        final String apiKey = System.getenv("ACOUSTICID_API_KEY");
        if (apiKey == null) {
            throw new IllegalStateException("Environment variable ACOUSTICID_API_KEY has not been set");
        }
        return apiKey;
    }

    /**
     * Returns the closest track id to the provided fingerprint.
     *
     * @param fingerprint fingerprint string
     * @param duration duration in seconds
     * @return acoustIDtrackID
     */
    @Override
    public String getClosestMatchID(String fingerprint, int duration) {
        final Song.SongMetadata metadata = getClosestMatchMetadata(fingerprint, duration);
        return metadata.acoustIDTrackID();
    }

    /**
     * Returns the metadata of the closest match.
     *
     * @param fingerprint fingerprint string
     * @param duration duration in seconds
     * @return songMetadata
     */
    @Override
    public Song.SongMetadata getClosestMatchMetadata(String fingerprint, int duration) {
        if (fingerprint == null) {
            throw new FingerprintMatchNotFoundException("Null parameters passed into query");
        }

        final CompletableFuture<Response> future = addAPIRequest(fingerprint, duration);

        try {
            final Response apiResult = future.get(
                    AcoustIDApiConstants.REQUEST_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            return parseJSONApiResults(apiResult);
        }
        catch (InterruptedException | ExecutionException | TimeoutException exception) {
            throw new FingerprintMatchNotFoundException(exception.toString());
        }
    }

    /**
     * Adds an API request corresponding to the fingerprint and duration to the queue.
     *
     * @param fingerprint string
     * @param duration in seconds
     * @return future
     */
    private synchronized CompletableFuture<Response> addAPIRequest(String fingerprint, int duration) {
        final String url = AcoustIDApiConstants.ACOUST_ID_API_URL
            + "?client=" + acoustIDApiKey
            + "&meta=" + AcoustIDApiConstants.METADATA_REQUEST
            + "&duration=" + duration
            + "&fingerprint=" + fingerprint;

        final Request request = new Request.Builder()
            .url(url)
            .build();

        final CompletableFuture<Response> future = new CompletableFuture<>();
        requestQueue.add(new QueuedRequest(request, future));

        if (!running) {
            running = true;
            processNextAPIRequest();
        }

        return future;
    }

    /**
     * Stores a request and the corresponding future that needs to be completed.
     *
     * @param request an HTTP request
     * @param future a promise
     */
    private record QueuedRequest(Request request, CompletableFuture<Response> future) {
    }
}
