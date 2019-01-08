/*
 * Copyright 2010-2019 Denis Nelubin and others.
 *
 * This file is part of Weather Notification.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ru.gelin.android.weather.notification.app;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.Log;

import ru.gelin.android.weather.Weather;
import ru.gelin.android.weather.notification.WeatherStorage;

import static ru.gelin.android.weather.notification.app.PreferenceKeys.REFRESH_INTERVAL;
import static ru.gelin.android.weather.notification.app.PreferenceKeys.REFRESH_INTERVAL_DEFAULT;

public class UpdateJobCreator {

    private static final int JOB_ID = 0;
    /** Verbose extra name for the service start intent. */
    public static String EXTRA_VERBOSE = "verbose";
    /** Force extra name for the service start intent. */
    public static String EXTRA_FORCE = "force";
    private final Context mContext;
    private boolean mForce = false;
    private boolean mExtraVerbose = false;

    public UpdateJobCreator(final Context context) {
        mContext = context;
    }

    private long getNextRefreshTime() {
        final Weather weather = new WeatherStorage(mContext).load();
        final SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(mContext);

        final long now = System.currentTimeMillis();
        final RefreshInterval interval = RefreshInterval.valueOf(preferences.getString(
            REFRESH_INTERVAL, REFRESH_INTERVAL_DEFAULT));;
        long nextUpdate = weather.getTime().getTime() + interval.getInterval();
        if (nextUpdate <= now) {
            nextUpdate = now + interval.getInterval();
        }

        return nextUpdate;
    }

    public void setForcedUpdate(final boolean forcedUpdate) {
        mForce = forcedUpdate;
    }

    public void setExtraVerbose(final boolean extraVerbose) {
        mExtraVerbose = extraVerbose;
    }

    private JobInfo.Builder build() {
        final ComponentName name = new ComponentName(mContext, UpdateService.class);
        final JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, name);
        final PersistableBundle bundle = new PersistableBundle(2);

        bundle.putBoolean(EXTRA_FORCE, mForce);
        bundle.putBoolean(EXTRA_VERBOSE, mExtraVerbose);

        builder.setPeriodic(getNextRefreshTime());
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPersisted(true);
        builder.setExtras(bundle);

        return builder;
        }

    private JobScheduler schedule() {
        JobScheduler scheduler =
            (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.cancel(JOB_ID);

        return scheduler;
    }

    private static final String TAG = "UpdateJobCreator";

    public int scheduleNow() {
        Log.e(TAG, "Scheduling now");

        return schedule().schedule(build().build());
    }
}
