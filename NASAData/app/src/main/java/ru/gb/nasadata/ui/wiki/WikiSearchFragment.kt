package ru.gb.nasadata.ui.wiki

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.transition.Slide
import androidx.transition.TransitionManager
import ru.gb.nasadata.databinding.WikiSearchFragmentBinding
import ru.gb.nasadata.util.hide
import ru.gb.nasadata.util.show

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
            binding.wikiResult.webViewClient = object: WebViewClient(){
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    binding.wikiProgress.show()
                    TransitionManager.beginDelayedTransition(
                        binding.root,
                        Slide(Gravity.END).apply {
                            duration = 100
                        })
                    view?.hide()
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.wikiProgress.hide()
                    TransitionManager.beginDelayedTransition(binding.root)
                    view?.show()
                    super.onPageFinished(view, url)
                }
            }
            binding.wikiResult.loadUrl(WIKIPEDIA_URL + binding.inputEditText.text.toString())
            activity?.let {
                val inputMethodManager =
                    it.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}