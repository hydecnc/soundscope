package org.wavelabs.soundscope.data_access;

/**
 * Stores constants for the AcousticID API
 */
public class AcousticIDAPIConstants {
    public final static String ACOUSTICID_API_URL = "https://api.acoustid.org/v2/lookup";
    public final static String METADATA_REQUEST = "recordings+releasegroups+compress";

    public final static String STATUS_CODE = "status";
    public final static String SUCCESS_CODE = "ok";
    public final static String RESULTS_CODE = "results";
    public final static String MATCH_QUALITY_CODE = "score";
    public final static String ACOUSTICID_TRACK_ID_CODE = "id";
    public final static String DURATION_CODE = "duration";
    public final static String MUSICBRAINZ_ID_CODE = "id";
    public final static String RECORDINGS_CODE = "recordings";
    public final static String SONG_TITLE_CODE = "title";
    public final static String RELEASES_CODE = "releasegroups";
    public final static String ALBUM_TITLE_CODE = "title";
    public final static String ARTISTS_CODE = "artists";
    public final static String ARTIST_NAME_CODE = "name";

    public final static String UNSUCCESSFUL_STATUS_MSG = "Status code unsuccessful";
    
    public final static long REQUEST_SPACING_MILLIS = 1000;
    public final static long REQUEST_TIMEOUT_MILLIS = 10000;
}
