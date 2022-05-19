package ru.gb.nasadata.ui.picture

import android.os.Bundle
import android.util.Log
import android.view.*
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

import ru.gb.nasadata.ui.settings.SettingsFragment
import ru.gb.nasadata.databinding.PictureOfTheDayFragmentBinding
import ru.gb.nasadata.ui.BottomNavigationDrawerFragment
import ru.gb.nasadata.ui.wiki.WikiSearchFragment
import ru.gb.nasadata.util.show
import ru.gb.nasadata.util.hide
import ru.gb.nasadata.util.toast
import java.text.SimpleDateFormat
import java.util.*


const val MEDIA_TYPE_VIDEO = "video"
const val DATE_TAG = "DATE_TAG"
const val DATE_ERROR = "Date error"
const val EMPTY_STRING = ""

class PictureOfTheDayFragment : Fragment() {

    private var _binding: PictureOfTheDayFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private val viewModel: PictureOfTheDayViewModel by lazy {
        ViewModelProvider(this)[PictureOfTheDayViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PictureOfTheDayFragmentBinding.inflate(inflater, container, false)

        var date: Date? = null
        arguments?.let{args ->
            args.getString(DATE_TAG)?.also { dateStr ->
                try {
                    date = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dateStr)
                }catch (e: Exception){
                    toast(DATE_ERROR)
                }
            }
        }
        viewModel.getData(date).observe(viewLifecycleOwner) { renderData(it) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun renderData(data: PictureOfTheDayData) {
        when (data) {
            is PictureOfTheDayData.Success -> {
                binding.progress.hide()
                val serverResponseData = data.serverResponseData
                val url = if(serverResponseData.mediaType != MEDIA_TYPE_VIDEO) serverResponseData.url
                    else serverResponseData.thumbnailUrl
                val description = serverResponseData.explanation ?: EMPTY_STRING
                var podDate = serverResponseData.date ?: EMPTY_STRING
                if (url.isNullOrEmpty()) {
                    toast(getString(R.string.link_is_empty))
                } else {
                    binding.imageView.load(url)
                    binding.imageDescription.text = description
                    binding.podDate.text = podDate
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

    companion object {
        fun newInstance(date: String? = null): PictureOfTheDayFragment{
            val fragment = PictureOfTheDayFragment()
            date?.let {
                fragment.arguments = Bundle().apply {
                    putString(DATE_TAG, it)
                }
            }
            return fragment
        }
    }
}
