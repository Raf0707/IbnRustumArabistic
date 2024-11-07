package ibn.rustum.arabistic.ui.words.search_word

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.google.android.material.snackbar.Snackbar
import ibn.rustum.arabistic.R
class SearchWordFragment : Fragment() {

    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Программно создаём WebView
        webView = WebView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Настраиваем WebView
        webView.settings.javaScriptEnabled = true

        // Получаем переданное слово из аргументов
        val word = arguments?.getString("searchWord") ?: ""

        // Загружаем URL с подставленным словом
        webView.loadUrl("http://arabus.ru/search/$word")

        //Snackbar.make(webView, "http://arabus.ru/search/$word", Snackbar.LENGTH_LONG).show()

        return webView
    }
}
