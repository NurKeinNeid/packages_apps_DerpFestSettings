package com.android.settings.gestures

import android.os.Bundle
import android.provider.Settings
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.android.settings.R
import com.android.settings.SettingsActivity
import com.android.settings.utils.CandidateInfoExtra
import com.android.settings.widget.MainSwitchBarController
import com.android.settings.widget.RadioButtonPickerFragment
import com.android.settingslib.widget.CandidateInfo
import com.android.settingslib.widget.SelectorWithWidgetPreference

class SwipeGesturePreferenceFragment : RadioButtonPickerFragment() {

  override fun bindPreference(
    pref: SelectorWithWidgetPreference,
    key: String,
    info: CandidateInfo,
    defaultKey: String,
  ): SelectorWithWidgetPreference {
    val boundPref = super.bindPreference(pref, key, info, defaultKey)
    (info as? CandidateInfoExtra)?.let {
      boundPref.setSummary(info.loadSummary())
    }
    return boundPref
  }

  override fun onRadioButtonConfirmed(selectedKey: String) {
    super.onRadioButtonConfirmed(selectedKey)
    val contentResolver = context!!.contentResolver
    when (selectedKey) {
      KEY_ASSIST_ACTION -> Settings.Secure.putInt(contentResolver, SETTING_ASSIST_ACTION, -1)
      KEY_SCREENSHOT_ACTION -> Settings.Secure.putInt(contentResolver, SETTING_ASSIST_ACTION, 0)
      KEY_CAMERA_ACTION -> Settings.Secure.putInt(contentResolver, SETTING_ASSIST_ACTION, 1)
      else -> {}
    }
  }

  override fun getCandidates(): List<CandidateInfo> {
    val resources = context!!.resources
    return listOf(
      CandidateInfoExtra(
        resources.getText(R.string.assistant_action_title),
        resources.getText(R.string.assistant_action_summary),
        KEY_ASSIST_ACTION,
        /* enabled= */ true,
      ),
      CandidateInfoExtra(
        resources.getText(R.string.screenshot_action_title),
        resources.getText(R.string.screenshot_action_summary),
        KEY_SCREENSHOT_ACTION,
        /* enabled= */ true,
      ),
      CandidateInfoExtra(
        resources.getText(R.string.camera_action_title),
        resources.getText(R.string.camera_action_summary),
        KEY_CAMERA_ACTION,
        /* enabled= */ true,
      ),
    )
  }

  override fun getDefaultKey(): String {
    val contentResolver = context!!.contentResolver
    return when (Settings.Secure.getInt(contentResolver, SETTING_ASSIST_ACTION, -1)) {
      0 -> KEY_SCREENSHOT_ACTION
      1 -> KEY_CAMERA_ACTION
      else -> KEY_ASSIST_ACTION
    }
  }

  override fun setDefaultKey(key: String): Boolean {
    val contentResolver = context!!.contentResolver
    when (key) {
      KEY_SCREENSHOT_ACTION -> Settings.Secure.putInt(contentResolver, SETTING_ASSIST_ACTION, 0)
      KEY_CAMERA_ACTION -> Settings.Secure.putInt(contentResolver, SETTING_ASSIST_ACTION, 1)
      else -> Settings.Secure.putInt(contentResolver, SETTING_ASSIST_ACTION, -1)
    }
    return true
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    val settingsActivity = activity as? SettingsActivity ?: return
    settingsActivity.switchBar.setTitle(context!!.getString(R.string.enable_corner_swipe_title))
    val checkedByDefault =
      context!!
        .resources
        .getBoolean(com.android.internal.R.bool.config_assistTouchGestureEnabledDefault)
    settingsActivity.switchBar.setChecked(
      Settings.Secure.getInt(
        context!!.contentResolver,
        Settings.Secure.ASSIST_TOUCH_GESTURE_ENABLED,
        if (checkedByDefault) 1 else 0,
      ) == 1
    )
    settingsActivity.switchBar.show()
    settingsLifecycle.addObserver(
      object : DefaultLifecycleObserver {
        val widgetController = MainSwitchBarController(settingsActivity.switchBar)

        init {
          widgetController.setListener { isChecked ->
            val resolver = this@SwipeGesturePreferenceFragment.context?.contentResolver
            resolver?.let {
              Settings.Secure.putInt(
                it,
                Settings.Secure.ASSIST_TOUCH_GESTURE_ENABLED,
                if (isChecked) 1 else 0,
              )
            }
            resolver != null
          }
        }

        override fun onPause(owner: LifecycleOwner) {
          widgetController.stopListening()
        }

        override fun onResume(owner: LifecycleOwner) {
          widgetController.startListening()
        }
      }
    )
  }

  override fun getMetricsCategory(): Int = 0

  override fun getPreferenceScreenResId(): Int = R.xml.swipe_gesture_preferences

  companion object {
    private const val SETTING_ASSIST_ACTION = "assist_action"
    private const val KEY_ASSIST_ACTION = "assistant"
    private const val KEY_CAMERA_ACTION = "camera"
    private const val KEY_SCREENSHOT_ACTION = "screenshot"
  }
}
