package ru.gb.nasadata.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomappbar.BottomAppBar
import ru.gb.nasadata.R
import ru.gb.nasadata.databinding.MainActivityBinding
import ru.gb.nasadata.ui.picture.PictureOfTheDayFragment
import ru.gb.nasadata.ui.settings.SettingsFragment
import ru.gb.nasadata.ui.wiki.WikiSearchFragment

const val SETTINGS_PREFS = "SETTINGS_PREFS"
const val NIGHT_MODE_TAG = "NIGHT_MODE_TAG"
const val MAIN_PICTURE_OF_THE_DAY_FRAGMENT_TAG = "MAIN_PICTURE_OF_THE_DAY_FRAGMENT"
const val BOTTOM_NAVIGATION_DRAWER_CONTENT = "BOTTOM_NAVIGATION_DRAWER_CONTENT"

class MainActivity :
    AppCompatActivity(),
    SettingsFragment.SwitchNightModeListener {

    private lateinit var binding: MainActivityBinding
    private var prefs: SharedPreferences? = null
    private var isMain = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBottomAppBar()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, PictureOfTheDayFragment.newInstance(), MAIN_PICTURE_OF_THE_DAY_FRAGMENT_TAG)
                .commit()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_bottom_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_settings -> supportFragmentManager.beginTransaction()
                .add(R.id.container, SettingsFragment()).addToBackStack(null).commit()
            R.id.app_bar_search -> {
                supportFragmentManager
                        .beginTransaction()
                        .add(R.id.container, WikiSearchFragment())
                        .addToBackStack(null)
                        .commit()
                binding.fab.performClick()
            }
            android.R.id.home -> {
                BottomNavigationDrawerFragment().show(supportFragmentManager, BOTTOM_NAVIGATION_DRAWER_CONTENT)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setBottomAppBar() {
        setSupportActionBar(binding.bottomAppBar)
        binding.fab.setOnClickListener {
            if (isMain) {
                isMain = false
                binding.bottomAppBar.navigationIcon = null
                binding.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
                binding.fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_back_fab))
                binding.bottomAppBar.replaceMenu(R.menu.menu_bottom_bar_other_screen)
            } else {
                isMain = true
                binding.bottomAppBar.navigationIcon =
                    ContextCompat.getDrawable(this, R.drawable.ic_photo_library)
                binding.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                binding.fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_plus_fab))
                binding.bottomAppBar.replaceMenu(R.menu.menu_bottom_bar)
            }
        }
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount > 1){
            supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }else{
            super.onBackPressed()
        }
    }
}