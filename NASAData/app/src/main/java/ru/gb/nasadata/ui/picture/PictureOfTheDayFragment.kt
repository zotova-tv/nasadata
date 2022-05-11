package ru.gb.nasadata.ui.picture

import android.app.Activity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.gb.nasadata.PictureOfTheDayViewModel
import ru.gb.nasadata.ui.MainActivity
import ru.gb.nasadata.R
import ru.gb.nasadata.viewmodel.PictureOfTheDayData

import ru.gb.nasadata.ui.chips.ChipsFragment
import ru.gb.nasadata.databinding.PictureOfTheDayFragmentBinding
import ru.gb.nasadata.ui.BottomNavigationDrawerFragment
import ru.gb.nasadata.util.show
import ru.gb.nasadata.util.hide


const val MEDIA_TYPE_VIDEO = "video"
const val BOTTOM_NAVIGATION_DRAWER_CONTENT = "BOTTOM_NAVIGATION_DRAWER_CONTENT"
const val WIKIPEDIA_URL = "https://en.wikipedia.org/wiki/"

class PictureOfTheDayFragment : Fragment() {

    private var _binding: PictureOfTheDayFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private val viewModel: PictureOfTheDayViewModel by lazy {
        ViewModelProvider(this).get(PictureOfTheDayViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PictureOfTheDayFragmentBinding.inflate(inflater, container, false)
        viewModel.getData().observe(viewLifecycleOwner) { renderData(it) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomSheetBehavior(binding.wikiBottomSheetLayout.wikiBottomSheetContainer)
        binding.inputLayout.setEndIconOnClickListener {
            binding.wikiBottomSheetLayout.wikiResult.webViewClient = WebViewClient()
            binding.wikiBottomSheetLayout.wikiResult.loadUrl(WIKIPEDIA_URL + binding.inputEditText.text.toString())
            binding.wikiBottomSheetLayout.wikiBottomSheetContainer.show()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            activity?.let {notNullActivity ->
                val inputMethodManager = notNullActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        setBottomAppBar(view)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_bottom_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_fav -> toast(getString(R.string.favourite))
            R.id.app_bar_settings -> activity?.supportFragmentManager?.beginTransaction()
                ?.add(R.id.container, ChipsFragment())?.addToBackStack(null)?.commit()
            android.R.id.home -> {
                activity?.let {
                    BottomNavigationDrawerFragment().show(it.supportFragmentManager, BOTTOM_NAVIGATION_DRAWER_CONTENT)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun renderData(data: PictureOfTheDayData) {
        when (data) {
            is PictureOfTheDayData.Success -> {
                binding.progress.hide()
                val serverResponseData = data.serverResponseData
                val url = if(serverResponseData.mediaType != MEDIA_TYPE_VIDEO) serverResponseData.url
                    else serverResponseData.thumbnailUrl
                val description = serverResponseData.explanation
                if (url.isNullOrEmpty()) {
                    toast(getString(R.string.link_is_empty))
                } else {
                    binding.imageView.load(url)
                    binding.imageDescription.text = description
                }
            }
            is PictureOfTheDayData.Loading -> {
                binding.progress.show()
            }
            is PictureOfTheDayData.Error -> {
                toast(data.error.message)
            }
        }
    }

    //
    private fun setBottomAppBar(view: View) {
        val context = activity as MainActivity
        context.setSupportActionBar(binding.bottomAppBar)
        setHasOptionsMenu(true)
        binding.fab.setOnClickListener {
            if (isMain) {
                isMain = false
                binding.bottomAppBar.navigationIcon = null
                binding.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
                binding.fab.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_back_fab))
                binding.bottomAppBar.replaceMenu(R.menu.menu_bottom_bar_other_screen)
            } else {
                isMain = true
                binding.bottomAppBar.navigationIcon =
                    ContextCompat.getDrawable(context, R.drawable.ic_hamburger_menu_bottom_bar)
                binding.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                binding.fab.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_plus_fab))
                binding.bottomAppBar.replaceMenu(R.menu.menu_bottom_bar)
            }
        }
    }

    private fun setBottomSheetBehavior(bottomSheet: ConstraintLayout) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun Fragment.toast(string: String?) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.BOTTOM, 0, 250)
            show()
        }
    }

    companion object {
        fun newInstance() = PictureOfTheDayFragment()
        private var isMain = true
    }
}
