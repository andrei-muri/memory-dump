package muri.memdumpbackend.flaskservice;

public class FlaskEndpoint {
    private static final String BASE_URL = "http://localhost:5000/api/markdown";
    public static final String CONVERT_TO_HTML_ENDPOINT = BASE_URL + "/html";
    public static final String GET_STATISTICS_ENDPOINT = BASE_URL + "/statistics";
}
