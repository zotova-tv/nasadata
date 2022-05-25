package ru.gb.nasadata.ui.picture

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import coil.clear
import coil.dispose
import coil.load
import ru.gb.nasadata.PictureOfTheDayViewModel
import ru.gb.nasadata.R
import ru.gb.nasadata.viewmodel.PictureOfTheDayData

import ru.gb.nasadata.databinding.PictureOfTheDayFragmentBinding
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
                    date = SimpleDateFormat(getString(R.string.pod_api_date_format), Locale.ENGLISH).parse(dateStr)
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

        setPODChipsClickListeners()

        binding.hdImage.setOnClickListener { v ->
            TransitionManager.beginDelayedTransition(binding.root)
            v.hide()
        }
    }

    private fun renderData(data: PictureOfTheDayData) {
        when (data) {
            is PictureOfTheDayData.Success -> {
                binding.progress.hide()
                val serverResponseData = data.serverResponseData
                val url = if(serverResponseData.mediaType != MEDIA_TYPE_VIDEO) serverResponseData.url
                    else serverResponseData.thumbnailUrl
                val description = serverResponseData.explanation ?: EMPTY_STRING
                val podDate = serverResponseData.date ?: EMPTY_STRING
                val title = serverResponseData.title ?: EMPTY_STRING
                var fullScreenUrl: String? = serverResponseData.hdurl
                if (url.isNullOrEmpty()) {
                    toast(getString(R.string.link_is_empty))
                } else {
                    binding.imageView.load(url)
                    binding.imageDescription.text = description
                    binding.podDate.text = podDate
                    binding.mainToolbar.title = title
                }
                fullScreenUrl?.let {
                    binding.hdImage.load(it)
                    binding.fullScreen.show()
                    binding.fullScreen.setOnClickListener {
                        TransitionManager.beginDelayedTransition(binding.root)
                        binding.hdImage.show()
                        binding.hdImage.scaleType = ImageView.ScaleType.CENTER_CROP
                    }
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

    private fun resetFullScreenFAB(){
        with(binding.fullScreen){
            hide()
            setOnClickListener(null)
        }
    }

    private fun setPODChipsClickListeners(){
        binding.podToday.setOnClickListener {
            resetFullScreenFAB()
            viewModel.getData().observe(viewLifecycleOwner) { renderData(it) }
        }
        binding.podYesterday.setOnClickListener {
            resetFullScreenFAB()
            val c = Calendar.getInstance()
            c.add(Calendar.DATE, -1)
            viewModel.getData(c.time).observe(viewLifecycleOwner) { renderData(it) }
        }
        binding.podByDate.setOnClickListener {
            resetFullScreenFAB()
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            activity?.let {
                val dpd = DatePickerDialog(it, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val c = Calendar.getInstance()
                    c.set(year, monthOfYear, dayOfMonth)
                    viewModel.getData(c.time).observe(viewLifecycleOwner) { renderData(it) }
                }, year, month, day).apply {
                    datePicker.maxDate = Calendar.getInstance().timeInMillis
                }
                dpd.show()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
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
