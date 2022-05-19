package ru.gb.nasadata.ui.wiki

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import ru.gb.nasadata.databinding.WikiSearchFragmentBinding

const val WIKIPEDIA_URL = "https://en.wikipedia.org/wiki/"

class WikiSearchFragment: Fragment() {
    private var _binding: WikiSearchFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = WikiSearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.inputLayout.setEndIconOnClickListener {
            binding.wikiResult.webViewClient = WebViewClient()
            binding.wikiResult.loadUrl(WIKIPEDIA_URL + binding.inputEditText.text.toString())
            activity?.let {notNullActivity ->
                val inputMethodManager = notNullActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }
}