package org.wavelabs.soundscope.data_access;

/**
 * Stores constants for the AcoustID API.
 */
public class AcoustIDApiConstants {
    public static final String ACOUST_ID_API_URL = "https://api.acoustid.org/v2/lookup";
    public static final String METADATA_REQUEST = "recordings+releasegroups+compress";

    public static final String STATUS_CODE = "status";
    public static final String SUCCESS_CODE = "ok";
    public static final String RESULTS_CODE = "results";
    public static final String MATCH_QUALITY_CODE = "score";
    public static final String ACOUST_ID_TRACK_ID_CODE = "id";
    public static final String DURATION_CODE = "duration";
    public static final String MUSICBRAINZ_ID_CODE = "id";
    public static final String RECORDINGS_CODE = "recordings";
    public static final String SONG_TITLE_CODE = "title";
    public static final String RELEASES_CODE = "releasegroups";
    public static final String ALBUM_TITLE_CODE = "title";
    public static final String ARTISTS_CODE = "artists";
    public static final String ARTIST_NAME_CODE = "name";

    public static final String UNSUCCESSFUL_STATUS_MSG = "Status code unsuccessful";

    public static final long REQUEST_SPACING_MILLIS = 1000;
    public static final long REQUEST_TIMEOUT_MILLIS = 10000;
}
