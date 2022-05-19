package ru.gb.nasadata.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.gb.nasadata.R
import ru.gb.nasadata.databinding.BottomNavigationLayoutBinding
import ru.gb.nasadata.ui.picture.PictureOfTheDayFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

const val PICTURE_OF_THE_DAY_BY_DATE_FRAGMENT_TAG = "PICTURE_OF_THE_DAY_BY_DATE_FRAGMENT"


class BottomNavigationDrawerFragment : BottomSheetDialogFragment() {

    private var _binding: BottomNavigationLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomNavigationLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.photo_today -> {
                    activity?.let{
                        openPictureOfTheDayFragment(it)
                        dismiss()
                    }
                }
                R.id.photo_yesterday -> {
                    val c = Calendar.getInstance()
                    c.add(Calendar.DATE, 1)
                    val localDate = LocalDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
                    val dateStr: String = localDate.format(DateTimeFormatter.ofPattern(getString(R.string.pod_api_date_format)))
                    activity?.let {
                        openPictureOfTheDayFragment(it, dateStr)
                        dismiss()
                    }
                }
                R.id.photo_by_date -> {
                    activity?.let{
                        val c = Calendar.getInstance()
                        val year = c.get(Calendar.YEAR)
                        val month = c.get(Calendar.MONTH)
                        val day = c.get(Calendar.DAY_OF_MONTH)

                        val dpd = DatePickerDialog(it, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                            var localDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                            val dateStr: String = localDate.format(DateTimeFormatter.ofPattern(getString(R.string.pod_api_date_format)))
                            openPictureOfTheDayFragment(it, dateStr)
                            dismiss()
                        }, year, month, day).apply {
                            datePicker.maxDate = Calendar.getInstance().timeInMillis
                        }
                        dpd.show()
                    }
                }
            }
            true
        }
    }

    private fun openPictureOfTheDayFragment(activity: FragmentActivity, dateStr: String? = null){
        activity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, PictureOfTheDayFragment.newInstance(dateStr), PICTURE_OF_THE_DAY_BY_DATE_FRAGMENT_TAG)
            .addToBackStack(null)
            .commit()
    }
}
