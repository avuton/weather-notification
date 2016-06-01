/*
 * Copyright 2010—2016 Denis Nelubin and others.
 *
 * This file is part of Weather Notification.
 *
 * Weather Notification is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Weather Notification is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Weather Notification.  If not, see http://www.gnu.org/licenses/.
 */

package ru.gelin.android.weather.source;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import ru.gelin.android.weather.WeatherException;
import ru.gelin.android.weather.notification.app.Tag;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 *  Abstract class for a weather source which uses HTTP for transport
 */
public class HttpWeatherSource {

    /** Main encoding */
    public static final String ENCODING = "UTF-8";

    /** Charset pattern */
    static final String CHARSET = "charset=";

    /** OK HTTP status */
    private static final int HTTP_STATUS_OK = 200;

    /** User Agent of the Weather Source */
    static final String USER_AGENT = "Weather Notification (Linux; Android)";

    /** HTTP client */
    private HttpClient client;

    /**
     *  Reads the content of the specified URL.
     */
    protected InputStreamReader getReaderForURL(String url) throws WeatherException {
        Log.d(Tag.TAG, "requesting " + url);
        HttpGet request;
        try {
            request = new HttpGet(url);
            request.setHeader("User-Agent", USER_AGENT);
            prepareRequest(request);
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
     *  Add necessary headers to the GET request.
     */
    protected void prepareRequest(HttpGet request) {
        //void implementation
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
