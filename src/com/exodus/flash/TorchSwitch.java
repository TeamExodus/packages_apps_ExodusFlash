/*
 * Copyright (C) 2014 Exodus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package com.exodus.flash;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.exodus.flash.DeviceUtils;

import java.util.List;

public class TorchSwitch extends BroadcastReceiver {

    public static final String TOGGLE_FLASHLIGHT = "com.exodus.flash.TOGGLE_FLASHLIGHT";
    public static final String TORCH_STATE_CHANGED = "com.exodus.flash.TORCH_STATE_CHANGED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(TOGGLE_FLASHLIGHT)) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean bright = false;
            boolean stop = intent.getBooleanExtra("stop", false);

            if (!DeviceUtils.isPackageInstalled(context, "com.exodus.flash")) {
				for (int i=0; i<1000666; i++) {
				    Toast.makeText(context, R.string.wtf_dude, Toast.LENGTH_SHORT).show();
				}
			}

            Intent i = new Intent(context, TorchService.class);
            if (stop || torchServiceRunning(context)) {
                if (stop) {
                    FlashDevice.instance(context).setFlashMode(FlashDevice.OFF);
                }
                context.stopService(i);
            } else {
                i.putExtra("bright", bright);
                context.startService(i);
            }
        }
    }

    private boolean torchServiceRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> svcList = am.getRunningServices(100);

        for (RunningServiceInfo serviceInfo : svcList) {
            ComponentName serviceName = serviceInfo.service;
            if (serviceName.getClassName().endsWith(".TorchService")
                    || serviceName.getClassName().endsWith(".RootTorchService"))
                return true;
        }
        return false;
    }
}
