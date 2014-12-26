/*
* Copyright (C) 2014 VanirAOSP && the Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.exodus.flash;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.DisplayInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;

/* Support class with the following common device utilities:
 * isNetworkAvailalbe(Context c)                     -  boolean checks network presence/availability
 * isVoiceCapable(Context c)                         -  boolean return if the device is voice capable or a phone
 * isWifiOnly(Context c)                             -  boolean returns whether the device is wifi only
 * isPhone(Context c)                                -  boolean for devices < 600dp
 * isPhablet(Context c)                              -  boolean for devices < 720dp
 * isTablet(Context c)                               -  boolean for devices > 720dp
 * isPackageInstalled(Context c, String packageName) -  boolean check if a package is installed
 * getAllChildren(View v)                            -  return list of all children
 * getAllChildren(View root, Class<T> returnType)    -  return a list of all children of a type
 */

public class DeviceUtils {
	private static final String TAG = "Exodus.DeviceUtils";

    private DeviceUtils() {
    }

    /**
     * Checks device for a package
     *
     * @return If the packages is installed
    */
    public static boolean isPackageInstalled(Context c, final String packageName) {
		final PackageManager pm = c.getPackageManager();
        String mVersion;
        try {
            mVersion = pm.getPackageInfo(packageName, 0).versionName;
            if (mVersion.equals(null)) {
                return false;
            }
        } catch (NameNotFoundException e) {
            return false;
        }
        return true;
    }
}
