package com.android.settings.overlay;

import android.content.Context;

import com.android.settings.accounts.AccountFeatureProvider;
import com.android.settings.fuelgauge.BatteryStatusFeatureProvider;
import com.android.settings.overlay.FeatureFactoryImpl;

import com.google.android.settings.accounts.AccountFeatureProviderGoogleImpl;
import com.android.settings.fuelgauge.BatteryStatusFeatureProviderDerpFestImpl;

public final class FeatureFactoryImplDerpFest extends FeatureFactoryImpl {

    private AccountFeatureProvider mAccountFeatureProvider;
    private BatteryStatusFeatureProvider mBatteryStatusFeatureProvider;

    @Override
    public AccountFeatureProvider getAccountFeatureProvider() {
        if (mAccountFeatureProvider == null) {
            mAccountFeatureProvider = new AccountFeatureProviderGoogleImpl();
        }
        return mAccountFeatureProvider;
    }
}
