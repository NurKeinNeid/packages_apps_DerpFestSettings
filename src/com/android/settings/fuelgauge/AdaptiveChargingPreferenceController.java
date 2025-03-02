package com.android.settings.fuelgauge;

import android.content.Context;

import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;

import org.derpfest.systemui.adaptivecharging.AdaptiveChargingManager;

public class AdaptiveChargingPreferenceController extends TogglePreferenceController {

    private AdaptiveChargingManager mAdaptiveChargingManager;
    private boolean mChecked;

    public AdaptiveChargingPreferenceController(Context context, String key) {
        super(context, key);
        mAdaptiveChargingManager = new AdaptiveChargingManager(context);
    }

    @Override
    public int getAvailabilityStatus() {
        return mAdaptiveChargingManager.isAvailable() ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public boolean isChecked() {
        return mAdaptiveChargingManager.getEnabled();
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);
        mChecked = isChecked();
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_battery;
    }

    @Override
    public boolean setChecked(boolean checked) {
        mAdaptiveChargingManager.setEnabled(checked);
        if (!checked) {
            mAdaptiveChargingManager.setAdaptiveChargingDeadline(-1);
        }
        if (mChecked != checked) {
            mChecked = checked;
            return true;
        }
        return true;
    }
} 
