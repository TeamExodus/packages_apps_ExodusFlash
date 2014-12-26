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
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

public class MainActivity extends Activity {

    private static final int ANIMATION_DURATION = 300;

    private Context mContext;

    private boolean mBright;
    private boolean mTorchOn;

    // Preferences
    private SharedPreferences mPrefs;

    private boolean mHasBrightSetting = false;

    private float mFullScreenScale;

    private final BroadcastReceiver mStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TorchSwitch.TORCH_STATE_CHANGED)) {
                mTorchOn = intent.getIntExtra("state", 0) != 0;
                if (mTorchOn) {
                    onFlashOn();
                } else {
                    onFlashOff();
                }
            }
        }
    };

    private void onFlashOn() {
        if (mFullScreenScale <= 0.0f) {
            mFullScreenScale = getMeasureScale();
        }
        getActionBar().hide();
    }

    private void onFlashOff() {
        getActionBar().show();
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().show();

        setContentView(R.layout.main);
    }

    @Override
    public void onPause() {
        unregisterReceiver(mStateReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        registerReceiver(mStateReceiver, new IntentFilter(TorchSwitch.TORCH_STATE_CHANGED));
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem brightness = menu.findItem(R.id.action_high_brightness);
        if (mHasBrightSetting) {
            brightness.setChecked(mBright);
        } else {
            brightness.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_about) {
            openAboutDialog();
            return true;
        } else if (menuItem.getItemId() == R.id.action_high_brightness) {
            boolean isChecked = false;
            menuItem.setChecked(isChecked = !menuItem.isChecked());
            if (isChecked && !mPrefs.contains("bright")) {
                // reverse reverse!
                menuItem.setChecked(!isChecked);
                openBrightDialog();
            } else if (isChecked) {
                mBright = true;
                mPrefs.edit().putBoolean("bright", true).commit();
            } else {
                mBright = false;
                mPrefs.edit().putBoolean("bright", false).commit();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFullScreenScale = getMeasureScale();
    }

    private void openAboutDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.aboutview, null);

        new AlertDialog.Builder(this)
                .setTitle(R.string.about_title)
                .setView(view)
                .setNegativeButton(R.string.about_close, null)
                .show();
    }

    private void openBrightDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.brightwarn, null);

        new AlertDialog.Builder(this)
                .setTitle(R.string.warning_label)
                .setView(view)
                .setNegativeButton(R.string.brightwarn_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .setPositiveButton(R.string.brightwarn_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mBright = true;
                        mPrefs.edit().putBoolean("bright", true).commit();
                    }
                })
                .show();
    }

    private float getMeasureScale() {
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float displayHeight = outMetrics.heightPixels;
        float displayWidth  = outMetrics.widthPixels;
        return (Math.max(displayHeight, displayWidth) /
                mContext.getResources().getDimensionPixelSize(R.dimen.button_size)) * 2;
    }
}
