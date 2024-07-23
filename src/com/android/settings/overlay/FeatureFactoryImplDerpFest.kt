package com.android.settings.overlay

import android.content.Context

import com.android.settings.accounts.AccountFeatureProvider
import com.android.settings.fuelgauge.BatteryStatusFeatureProvider
import com.android.settings.overlay.FeatureFactoryImpl

import com.google.android.settings.accounts.AccountFeatureProviderGoogleImpl

class FeatureFactoryImplDerpFest : FeatureFactoryImpl() {
    override val accountFeatureProvider: AccountFeatureProvider by lazy {
        AccountFeatureProviderGoogleImpl()
    }
}
