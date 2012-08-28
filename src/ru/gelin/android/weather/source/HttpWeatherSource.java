package ru.gelin.android.weather.source;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import ru.gelin.android.weather.WeatherException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 *  Abstract class for a weather source which uses HTTP for transport
 */
public class HttpWeatherSource {

    /** Main encoding */
    protected static final String ENCODING = "UTF-8";

    /** Charset pattern */
    static final String CHARSET = "charset=";

    /** OK HTTP status */
    private static final int HTTP_STATUS_OK = 200;

    /** User Agent of the Weather Source */
    static final String USER_AGENT = "Google Weather/1.0 (Linux; Android)";

    /** HTTP client */
    private HttpClient client;

    /**
     *  Reads the content of the specified URL.
     */
    protected InputStreamReader getReaderForURL(String url) throws WeatherException {
        HttpGet request;
        try {
            request = new HttpGet(url);
            request.setHeader("User-Agent", USER_AGENT);
        } catch (Exception e) {
            throw new WeatherException("Can't prepare http request", e);
        }

        String charset = ENCODING;
        try {
            HttpResponse response = getClient().execute(request);

            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() != HTTP_STATUS_OK) {
                throw new WeatherException("Invalid response from server: " +
                        status.toString());
            }

            HttpEntity entity = response.getEntity();
            charset = HttpUtils.getCharset(entity);
            InputStreamReader inputStream = new InputStreamReader(entity.getContent(), charset);

            return inputStream;
        } catch (UnsupportedEncodingException uee) {
            throw new WeatherException("unsupported charset: " + charset, uee);
        } catch (IOException e) {
            throw new WeatherException("Problem communicating with API", e);
        }
    }

    /**
     *  Creates client with specific user-agent string
     */
    HttpClient getClient() throws WeatherException {
        if (this.client == null) {
            try {
                this.client = new DefaultHttpClient();
            } catch (Exception e) {
                throw new WeatherException("Can't prepare http client", e);
            }
        }
        return this.client;
    }

}