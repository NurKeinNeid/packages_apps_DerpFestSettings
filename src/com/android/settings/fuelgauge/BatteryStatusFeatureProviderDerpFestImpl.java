package com.android.settings.fuelgauge;

import android.content.Context;
import android.database.ContentObserver;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.fuelgauge.BatteryInfo;
import com.android.settings.fuelgauge.BatteryPreferenceController;
import com.android.settings.fuelgauge.BatteryStatusFeatureProviderImpl;

import org.derpfest.systemui.adaptivecharging.AdaptiveChargingManager;

import java.util.concurrent.TimeUnit;

public class BatteryStatusFeatureProviderDerpFestImpl extends BatteryStatusFeatureProviderImpl {
    private boolean mAdaptiveChargingEnabledInSettings;
    private AdaptiveChargingManager mAdaptiveChargingManager;

    public BatteryStatusFeatureProviderDerpFestImpl(Context context) {
        super(context);
        mAdaptiveChargingManager = new AdaptiveChargingManager(context);
        mContext.getContentResolver()
                .registerContentObserver(
                        Settings.Secure.getUriFor("adaptive_charging_enabled"),
                        false,
                        new ContentObserver(null) {
                            @Override
                            public void onChange(boolean changed) {
                                refreshAdaptiveChargingEnabled();
                            }
                        });
        refreshAdaptiveChargingEnabled();
    }

    private void refreshAdaptiveChargingEnabled() {
        mAdaptiveChargingEnabledInSettings =
                mAdaptiveChargingManager.isAvailable() && mAdaptiveChargingManager.getEnabled();
    }

    @Override
    public boolean triggerBatteryStatusUpdate(
            final BatteryPreferenceController batteryPreferenceController,
            final BatteryInfo batteryInfo) {
        if (batteryInfo.discharging || !mAdaptiveChargingEnabledInSettings) {
            return false;
        } else {
            mAdaptiveChargingManager.queryStatus(
                    new AdaptiveChargingManager.AdaptiveChargingStatusReceiver() {
                        private boolean mSetStatus;

                        @Override
                        public void onReceiveStatus(int seconds, String stage) {
                            if (AdaptiveChargingManager.isActive(stage, seconds)) {
                                batteryPreferenceController.updateBatteryStatus(
                                        mContext.getResources()
                                                .getString(
                                                        R.string.adaptive_charging_time_estimate,
                                                        mAdaptiveChargingManager.formatTimeToFull(
                                                                System.currentTimeMillis()
                                                                        + TimeUnit.SECONDS.toMillis(
                                                                                seconds + 29))),
                                        batteryInfo);
                                mSetStatus = true;
                            }
                        }

                        @Override
                        public void onDestroyInterface() {
                            if (mSetStatus) {
                                return;
                            }
                            batteryPreferenceController.updateBatteryStatus(null, batteryInfo);
                        }
                    });
            return true;
        }
    }
}
