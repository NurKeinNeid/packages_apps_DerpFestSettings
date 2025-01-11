package com.android.settings.gestures

import android.content.Context
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import com.android.settings.R
import com.android.settings.core.BasePreferenceController
import com.android.settingslib.PrimarySwitchPreference

class SwipeGesturePreferenceController(private val context: Context) :
  BasePreferenceController(context, SWIPE_GESTURE_PREFERENCE_KEY),
  Preference.OnPreferenceChangeListener {

  private var primaryPreference: PrimarySwitchPreference? = null

  override fun getAvailabilityStatus() = AVAILABLE

  override fun displayPreference(screen: PreferenceScreen) {
    super.displayPreference(screen)
    primaryPreference = screen.findPreference(preferenceKey) as? PrimarySwitchPreference ?: return
    updateChecked()
    updateSummary()
  }

  override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
    val newChecked = newValue as Boolean
    (preference as PrimarySwitchPreference).setChecked(newChecked)
    Settings.Secure.putInt(
      preference.context.contentResolver,
      Settings.Secure.ASSIST_TOUCH_GESTURE_ENABLED,
      if (newChecked) 1 else 0,
    )
    return true
  }

  override fun updateState(preference: Preference) {
    updateChecked()
    updateSummary()
  }

  private fun updateChecked() {
    val checkedByDefault =
      context.resources.getBoolean(
        com.android.internal.R.bool.config_assistTouchGestureEnabledDefault
      )
    primaryPreference?.setChecked(
      Settings.Secure.getInt(
        context.contentResolver,
        Settings.Secure.ASSIST_TOUCH_GESTURE_ENABLED,
        if (checkedByDefault) 1 else 0,
      ) == 1
    )
  }

  private fun updateSummary() {
    val assistAction =
      Settings.Secure.getInt(context.contentResolver, ASSIST_ACTION_PREFERENCE, /*def =*/ -1)
    val summary =
      context.getString(
        when (assistAction) {
          0 -> R.string.screenshot_corner_gesture_summary
          else -> R.string.assistant_corner_gesture_summary
        }
      )
    primaryPreference?.setSummary(summary)
  }


  companion object {
    private const val SWIPE_GESTURE_PREFERENCE_KEY = "swipe_gesture_picker"
    private const val ASSIST_ACTION_PREFERENCE = "assist_action"
  }
}
