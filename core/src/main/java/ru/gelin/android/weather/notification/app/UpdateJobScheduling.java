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

package ru.gelin.android.weather.notification.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import ru.gelin.android.weather.notification.AppUtils;
import ru.gelin.android.weather.notification.Tag;

/**
 *  Broadcast receiver which receives event about changes of network connectivity.
 *  Starts UpdateService if the network goes up.
 */
public class UpdateJobScheduling extends BroadcastReceiver {

    /** Main app package name */
    private static final String APP_PACKAGE_NAME = Tag.class.getPackage().getName();

    /** Intent action to start the main activity */
    public static String ACTION_START_UPDATE_JOB_SERVICE =
        APP_PACKAGE_NAME + ".ACTION_START_UPDATE_JOB_SERVICE";

    @Override
    public void onReceive (Context context, Intent intent) {
        Log.e("UpdateJobScheduling", "Updating job");

        final UpdateJobCreator updateJobCreator = new UpdateJobCreator(context);
        final boolean extraVerbose = intent.getBooleanExtra(UpdateJobCreator.EXTRA_VERBOSE, false);
        final boolean forceUpdate = intent.getBooleanExtra(UpdateJobCreator.EXTRA_FORCE, true);

        updateJobCreator.setExtraVerbose(extraVerbose);
        updateJobCreator.setForcedUpdate(forceUpdate);

        updateJobCreator.scheduleNow();
    }
}
