package ru.gb.nasadata.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import ru.gb.nasadata.R
import ru.gb.nasadata.ui.picture.PictureOfTheDayFragment
import ru.gb.nasadata.ui.settings.SettingsFragment

const val SETTINGS_PREFS = "SETTINGS_PREFS"
const val NIGHT_MODE_TAG = "NIGHT_MODE_TAG"

class MainActivity :
    AppCompatActivity(),
    SettingsFragment.SwitchNightModeListener {

    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PictureOfTheDayFragment.newInstance())
                .commitNow()
        }

        prefs = getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
        val nightModeIsChecked = prefs?.getBoolean(NIGHT_MODE_TAG, false) ?: false
        switchNightMode(nightModeIsChecked)
    }

    override fun onSwitchNightMode(isChecked: Boolean) {
        prefs?.let {
            it.edit().also {editor ->
                editor.putBoolean(NIGHT_MODE_TAG, isChecked)
                editor.apply()
            }
        }
        switchNightMode(isChecked)
    }

    private fun switchNightMode(isChecked: Boolean){
        when (isChecked) {
            true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}