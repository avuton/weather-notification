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

package ru.gelin.android.weather.notification;

import android.app.Instrumentation;
import android.content.Context;
import android.os.Parcel;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import ru.gelin.android.weather.Cloudiness;
import ru.gelin.android.weather.CloudinessUnit;
import ru.gelin.android.weather.Humidity;
import ru.gelin.android.weather.PrecipitationPeriod;
import ru.gelin.android.weather.Temperature;
import ru.gelin.android.weather.TemperatureUnit;
import ru.gelin.android.weather.Weather;
import ru.gelin.android.weather.WeatherCondition;
import ru.gelin.android.weather.WeatherConditionType;
import ru.gelin.android.weather.Wind;
import ru.gelin.android.weather.WindDirection;
import ru.gelin.android.weather.WindSpeedUnit;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;

import static android.test.AndroidTestCase.assertEquals;
import static android.test.AndroidTestCase.assertTrue;

public class WeatherUtils {
    
    public static enum Version {
        V_0_2, V_0_3
    }
    
    private WeatherUtils() {
        //avoid instantiation
    }

    public static Weather createOpenWeather(Instrumentation instrumentation) throws Exception {
        return ru.gelin.android.weather.openweathermap.WeatherUtils.createOpenWeather(instrumentation);
    }

    public static Weather createIncompleteOpenWeather(Instrumentation instrumentation) throws Exception {
        return ru.gelin.android.weather.openweathermap.WeatherUtils.createIncompleteOpenWeather(instrumentation);
    }

    public static ru.gelin.android.weather.v_0_2.Weather createWeather_v_0_2(Context context) throws Exception {
        return new ru.gelin.android.weather.v_0_2.google.GoogleWeather(
                new InputStreamReader(context.getAssets().open("google_weather_api_en.xml")));
    }

    public static ru.gelin.android.weather.v_0_2.Weather convert(Weather weather) {
        Parcel parcel = Parcel.obtain();
        ParcelableWeather parcelable = new ParcelableWeather(weather);
        parcelable.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        return ru.gelin.android.weather.v_0_2.notification.ParcelableWeather.CREATOR.createFromParcel(parcel);
    }

    public static Weather convert(ru.gelin.android.weather.v_0_2.Weather weather) {
        Parcel parcel = Parcel.obtain();
        ru.gelin.android.weather.v_0_2.notification.ParcelableWeather parcelable =
                new ru.gelin.android.weather.v_0_2.notification.ParcelableWeather(weather);
        parcelable.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        return ParcelableWeather.CREATOR.createFromParcel(parcel);
    }
    
    public static void checkWeather(Weather weather, Version version) throws MalformedURLException {
        switch (version) {
        case V_0_2:
            checkWeather_v_0_2(weather);
            break;
        default:
            checkOpenWeather(weather);
        }
    }

    public static void checkOpenWeather(Weather weather) throws MalformedURLException {
        assertEquals("Omsk", weather.getLocation().getText());

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(2013, Calendar.AUGUST, 15, 14, 00, 00);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(calendar.getTime(), weather.getTime());
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.MINUTE, -1);
        assertTrue(weather.getQueryTime().after(calendar.getTime()));

        assertEquals(new URL("http://openweathermap.org/city/1496153"), weather.getForecastURL());

        assertEquals(4, weather.getConditions().size());

        WeatherCondition condition0 = weather.getConditions().get(0);
        assertEquals("Sky is clear", condition0.getConditionText());
        Temperature temp0 = condition0.getTemperature(TemperatureUnit.K);
        assertEquals(294, temp0.getCurrent());
        assertEquals(287, temp0.getLow());
        assertEquals(294, temp0.getHigh());
        Humidity humidity = condition0.getHumidity();
        assertEquals("Humidity: 56%", humidity.getText());
        assertEquals(56, humidity.getValue());
        Wind wind = condition0.getWind(WindSpeedUnit.MPS);
        assertEquals("Wind: N, 4 m/s", wind.getText());
        assertEquals(WindDirection.N, wind.getDirection());
        assertEquals(4, wind.getSpeed());
        Cloudiness cloudiness = condition0.getCloudiness(CloudinessUnit.PERCENT);
        assertEquals(0, cloudiness.getValue());
        assertEquals(0f, condition0.getPrecipitation().getValue(PrecipitationPeriod.PERIOD_1H), 0.01f);
        assertEquals(1, condition0.getConditionTypes().size());
        assertTrue(condition0.getConditionTypes().contains(WeatherConditionType.CLOUDS_CLEAR));

        WeatherCondition condition1 = weather.getConditions().get(1);
        assertEquals("Light rain", condition1.getConditionText());
        Temperature temp1 = condition1.getTemperature(TemperatureUnit.K);
        assertEquals(289, temp1.getCurrent());
        assertEquals(284, temp1.getLow());
        assertEquals(293, temp1.getHigh());
        assertEquals(98, condition1.getHumidity().getValue());
        assertEquals(WindDirection.N, condition1.getWind().getDirection());
        assertEquals(2, condition1.getWind().getSpeed());
        assertEquals(18, condition1.getCloudiness().getValue());
        assertEquals(1f, condition1.getPrecipitation().getValue(PrecipitationPeriod.PERIOD_1H), 0.01f);
        assertEquals(2, condition1.getConditionTypes().size());
        assertTrue(condition1.getConditionTypes().contains(WeatherConditionType.CLOUDS_FEW));
        assertTrue(condition1.getConditionTypes().contains(WeatherConditionType.RAIN_LIGHT));

        WeatherCondition condition2 = weather.getConditions().get(2);
        assertEquals("Rain", condition2.getConditionText());
        Temperature temp2 = condition2.getTemperature(TemperatureUnit.K);
        assertEquals(288, temp2.getCurrent());
        assertEquals(282, temp2.getLow());
        assertEquals(293, temp2.getHigh());
        assertEquals(93, condition2.getHumidity().getValue());
        assertEquals(WindDirection.SW, condition2.getWind().getDirection());
        assertEquals(2, condition2.getWind().getSpeed());
        assertEquals(0, condition2.getCloudiness().getValue());
        assertEquals(2f, condition2.getPrecipitation().getValue(PrecipitationPeriod.PERIOD_1H), 0.01f);
        assertEquals(2, condition2.getConditionTypes().size());
        assertTrue(condition2.getConditionTypes().contains(WeatherConditionType.CLOUDS_BROKEN));
        assertTrue(condition2.getConditionTypes().contains(WeatherConditionType.RAIN));

        WeatherCondition condition3 = weather.getConditions().get(3);
        assertEquals("Shower rain", condition3.getConditionText());
        Temperature temp3 = condition3.getTemperature(TemperatureUnit.K);
        assertEquals(289, temp3.getCurrent());
        assertEquals(283, temp3.getLow());
        assertEquals(295, temp3.getHigh());
        assertEquals(90, condition3.getHumidity().getValue());
        assertEquals(WindDirection.SW, condition3.getWind().getDirection());
        assertEquals(2, condition3.getWind().getSpeed());
        assertEquals(22, condition3.getCloudiness().getValue());
        assertEquals(3f, condition3.getPrecipitation().getValue(PrecipitationPeriod.PERIOD_1H), 0.01f);
        assertEquals(2, condition3.getConditionTypes().size());
        assertTrue(condition3.getConditionTypes().contains(WeatherConditionType.CLOUDS_OVERCAST));
        assertTrue(condition3.getConditionTypes().contains(WeatherConditionType.RAIN_SHOWER));
    }
    
    public static void checkWeather(ru.gelin.android.weather.v_0_2.Weather weather) {
        assertEquals("Omsk, Omsk Oblast", weather.getLocation().getText());
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(2010, Calendar.DECEMBER, 28, 6, 0, 0);
        assertEquals(calendar.getTime(), weather.getTime());
        assertEquals(ru.gelin.android.weather.v_0_2.UnitSystem.US, weather.getUnitSystem());
        assertEquals(4, weather.getConditions().size());
        
        ru.gelin.android.weather.v_0_2.WeatherCondition condition0 = weather.getConditions().get(0);
        assertEquals("Clear", condition0.getConditionText());
        ru.gelin.android.weather.v_0_2.Temperature temp0 =
            condition0.getTemperature(ru.gelin.android.weather.v_0_2.UnitSystem.US);
        assertEquals(-11, temp0.getCurrent());
        assertEquals(-10, temp0.getLow());
        assertEquals(-4, temp0.getHigh());
        assertEquals("Humidity: 66%", condition0.getHumidityText());
        assertEquals("Wind: SW at 2 mph", condition0.getWindText());
        
        ru.gelin.android.weather.v_0_2.WeatherCondition condition1 = weather.getConditions().get(1);
        assertEquals("Snow Showers", condition1.getConditionText());
        ru.gelin.android.weather.v_0_2.Temperature temp1 =
            condition1.getTemperature(ru.gelin.android.weather.v_0_2.UnitSystem.US);
        assertEquals(7, temp1.getCurrent());
        assertEquals(-7, temp1.getLow());
        assertEquals(20, temp1.getHigh());
        
        ru.gelin.android.weather.v_0_2.WeatherCondition condition2 = weather.getConditions().get(2);
        assertEquals("Partly Sunny", condition2.getConditionText());
        ru.gelin.android.weather.v_0_2.Temperature temp2 =
            condition2.getTemperature(ru.gelin.android.weather.v_0_2.UnitSystem.US);
        assertEquals(-10, temp2.getCurrent());
        assertEquals(-14, temp2.getLow());
        assertEquals(-6, temp2.getHigh());
        
        ru.gelin.android.weather.v_0_2.WeatherCondition condition3 = weather.getConditions().get(3);
        assertEquals("Partly Sunny", condition3.getConditionText());
        ru.gelin.android.weather.v_0_2.Temperature temp3 =
            condition3.getTemperature(ru.gelin.android.weather.v_0_2.UnitSystem.US);
        assertEquals(-22, temp3.getCurrent());
        assertEquals(-29, temp3.getLow());
        assertEquals(-15, temp3.getHigh());
    }
    
    @SuppressWarnings("deprecation")
    public static void checkWeather_v_0_2(Weather weather) {
        assertEquals("Omsk, Omsk Oblast", weather.getLocation().getText());
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(2010, Calendar.DECEMBER, 28, 6, 0, 0);
        assertEquals(calendar.getTime(), weather.getTime());
        assertEquals(4, weather.getConditions().size());
        
        WeatherCondition condition0 = weather.getConditions().get(0);
        assertEquals("Clear", condition0.getConditionText());
        Temperature temp0 = condition0.getTemperature();
        assertEquals(-11, temp0.getCurrent());
        assertEquals(-10, temp0.getLow());
        assertEquals(-4, temp0.getHigh());
        assertEquals("Humidity: 66%", condition0.getHumidityText());
        assertEquals("Wind: SW at 2 mph", condition0.getWindText());
        
        WeatherCondition condition1 = weather.getConditions().get(1);
        assertEquals("Snow Showers", condition1.getConditionText());
        Temperature temp1 = condition1.getTemperature();
        assertEquals(7, temp1.getCurrent());
        assertEquals(-7, temp1.getLow());
        assertEquals(20, temp1.getHigh());
        
        WeatherCondition condition2 = weather.getConditions().get(2);
        assertEquals("Partly Sunny", condition2.getConditionText());
        Temperature temp2 = condition2.getTemperature();
        assertEquals(-10, temp2.getCurrent());
        assertEquals(-14, temp2.getLow());
        assertEquals(-6, temp2.getHigh());
        
        WeatherCondition condition3 = weather.getConditions().get(3);
        assertEquals("Partly Sunny", condition3.getConditionText());
        Temperature temp3 = condition3.getTemperature();
        assertEquals(-22, temp3.getCurrent());
        assertEquals(-29, temp3.getLow());
        assertEquals(-15, temp3.getHigh());
    }

    public static JSONObject readJSON(Context context, String assetName) throws IOException, JSONException {
        Reader reader = new InputStreamReader(context.getAssets().open(assetName));
        StringBuilder buffer = new StringBuilder();
        int c = reader.read();
        while (c >= 0) {
            buffer.append((char)c);
            c = reader.read();
        }
        JSONTokener parser = new JSONTokener(buffer.toString());
        return (JSONObject)parser.nextValue();
    }

}
