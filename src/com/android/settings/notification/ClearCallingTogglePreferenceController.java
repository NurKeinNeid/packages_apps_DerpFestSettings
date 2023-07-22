package com.android.settings.notification;

import android.content.Context;
import android.media.AudioManager;

import com.android.settings.R;
import com.android.settings.widget.SettingsMainSwitchPreferenceController;

public class ClearCallingTogglePreferenceController extends SettingsMainSwitchPreferenceController {
    private static final String CCA_ENABLED_FLAG = "CcaConfig__is_enabled";
    private static final String CCA_STATUS_KEY = "cca_pixel_enabled";
    private static final String CCA_STATUS_OFF = "cca_pixel_enabled=false";
    static final String CCA_STATUS_ON = "cca_pixel_enabled=true";
    private final AudioManager mAudioManager;

    public ClearCallingTogglePreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
        mAudioManager = (AudioManager) context.getSystemService(AudioManager.class);
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_sound;
    }

    @Override
    public int getAvailabilityStatus() {
        if (mContext.getResources().getBoolean(R.bool.config_clear_calling_enabled)) {
            return AVAILABLE;
        }
        return DISABLED_DEPENDENT_SETTING;
    }

    @Override
    public boolean isChecked() {
        return mAudioManager.getParameters(CCA_STATUS_KEY).contains(CCA_STATUS_ON);
    }

    @Override
    public boolean setChecked(boolean checked) {
        mAudioManager.setParameters(checked ? CCA_STATUS_ON : CCA_STATUS_OFF);
        return true;
    }
}
