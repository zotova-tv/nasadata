package ru.gb.nasadata.ui

import android.animation.ObjectAnimator
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

const val WIKI_SEARCH_FRAGMENT_TAG = "WIKI_SEARCH_FRAGMENT_TAG"
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
                        .replace(R.id.container, WikiSearchFragment(), WIKI_SEARCH_FRAGMENT_TAG)
                        .addToBackStack(null)
                        .commit()
//                binding.fab.performClick()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setBottomAppBar() {
        setSupportActionBar(binding.bottomAppBar)
        binding.fab.setOnClickListener {
            if (isMain) {
                isMain = false
                binding.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
                ObjectAnimator.ofFloat(binding.fab, "rotation", 0f, 225f).start()

                binding.bottomAppBar.replaceMenu(R.menu.menu_bottom_bar_other_screen)
            } else {
                isMain = true
                binding.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                ObjectAnimator.ofFloat(binding.fab, "rotation", 0f, -180f).start()
                binding.bottomAppBar.replaceMenu(R.menu.menu_bottom_bar)
                supportFragmentManager.findFragmentByTag(WIKI_SEARCH_FRAGMENT_TAG)?.also {
                    supportFragmentManager.popBackStack()
                }
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