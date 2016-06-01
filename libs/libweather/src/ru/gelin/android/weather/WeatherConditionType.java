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

package ru.gelin.android.weather;

import static ru.gelin.android.weather.WeatherConditionTypePriority.*;

/**
 *  Enumeration of declared condition conditionTypes.
 */
public enum WeatherConditionType {

    THUNDERSTORM_RAIN_LIGHT(THUNDERSTORM_PRIORITY, 7),
    THUNDERSTORM_RAIN(THUNDERSTORM_PRIORITY, 8),
    THUNDERSTORM_RAIN_HEAVY(THUNDERSTORM_PRIORITY, 9),
    THUNDERSTORM_LIGHT(THUNDERSTORM_PRIORITY, 0),
    THUNDERSTORM(THUNDERSTORM_PRIORITY, 1),
    THUNDERSTORM_HEAVY(THUNDERSTORM_PRIORITY, 2),
    THUNDERSTORM_RAGGED(THUNDERSTORM_PRIORITY, 3),
    THUNDERSTORM_DRIZZLE_LIGHT(THUNDERSTORM_PRIORITY, 4),
    THUNDERSTORM_DRIZZLE(THUNDERSTORM_PRIORITY, 5),
    THUNDERSTORM_DRIZZLE_HEAVY(THUNDERSTORM_PRIORITY, 6),
    
    DRIZZLE_LIGHT(DRIZZLE_PRIORITY, 0),
    DRIZZLE(DRIZZLE_PRIORITY, 1),
    DRIZZLE_HEAVY(DRIZZLE_PRIORITY, 2),
    DRIZZLE_RAIN_LIGHT(DRIZZLE_PRIORITY, 3),
    DRIZZLE_RAIN(DRIZZLE_PRIORITY, 4),
    DRIZZLE_RAIN_HEAVY(DRIZZLE_PRIORITY, 5),
    DRIZZLE_SHOWER(DRIZZLE_PRIORITY, 6),

    RAIN_LIGHT(RAIN_PRIORITY, 0),
    RAIN(RAIN_PRIORITY, 1),
    RAIN_HEAVY(RAIN_PRIORITY, 2),
    RAIN_VERY_HEAVY(RAIN_PRIORITY, 3),
    RAIN_EXTREME(RAIN_PRIORITY, 4),
    RAIN_FREEZING(RAIN_PRIORITY, 8),
    RAIN_SHOWER_LIGHT(RAIN_PRIORITY, 5),
    RAIN_SHOWER(RAIN_PRIORITY, 6),
    RAIN_SHOWER_HEAVY(RAIN_PRIORITY, 7),

    SNOW_LIGHT(SNOW_PRIORITY, 0),
    SNOW(SNOW_PRIORITY, 1),
    SNOW_HEAVY(SNOW_PRIORITY, 2),
    SLEET(SNOW_PRIORITY, 4),
    SNOW_SHOWER(SNOW_PRIORITY, 3),
    
    MIST(ATMOSPHERE_PRIORITY, -1),
    SMOKE(ATMOSPHERE_PRIORITY, 0),
    HAZE(ATMOSPHERE_PRIORITY, 0),
    SAND_WHIRLS(ATMOSPHERE_PRIORITY, 0),
    FOG(ATMOSPHERE_PRIORITY, 0),

    CLOUDS_CLEAR(CLOUDS_PRIORITY, 0),
    CLOUDS_FEW(CLOUDS_PRIORITY, 1),
    CLOUDS_SCATTERED(CLOUDS_PRIORITY, 2),
    CLOUDS_BROKEN(CLOUDS_PRIORITY, 3),
    CLOUDS_OVERCAST(CLOUDS_PRIORITY, 4),
    
    TORNADO(EXTREME_PRIORITY, 0),
    TROPICAL_STORM(EXTREME_PRIORITY, 0),
    HURRICANE(EXTREME_PRIORITY, 0),
    COLD(EXTREME_PRIORITY, 0),
    HOT(EXTREME_PRIORITY, 0),
    WINDY(EXTREME_PRIORITY, 0),
    HAIL(EXTREME_PRIORITY, 0);


    private int priority = -1;
    private int strength = 0;

    private WeatherConditionType(int priority, int strength) {
        this.priority = priority;
        this.strength = strength;
    }

    public int getPriority() {
        return this.priority;
    }

    public int getStrength() {
        return this.strength;
    }

}
