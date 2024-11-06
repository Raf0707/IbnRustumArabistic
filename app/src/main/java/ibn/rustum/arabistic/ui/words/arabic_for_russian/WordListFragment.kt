package ibn.rustum.arabistic.ui.words.arabic_for_russian

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ibn.rustum.arabistic.R

import android.util.Log
import android.widget.FrameLayout

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class WordListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = requireContext()
        val lessonNumber = arguments?.getInt("lesson_number") ?: 1
        val linearLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        Log.d("WordListFragment", "Starting data load for lesson $lessonNumber")

        // Запуск корутины для загрузки данных
        lifecycleScope.launch {
            val jsonData = loadJsonDataAsync(lessonNumber+1)
            if (jsonData != null) {
                Log.d("WordListFragment", "Data loaded successfully, adding cards")
                jsonData.forEach { item ->
                    val cardView = createWordCard(context, item)
                    linearLayout.addView(cardView)
                    Log.d("WordListFragment", "Added card for word: ${item.singular_ru}")
                }
            } else {
                Log.e("WordListFragment", "Failed to load data for lesson $lessonNumber")
            }
        }

        return ScrollView(context).apply {
            val frameLayout = FrameLayout(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    setMargins(0, 25.dpToPx(), 0, 100.dpToPx())
                }
                addView(linearLayout)
            }
            addView(frameLayout)
        }
    }

    // Асинхронная загрузка JSON-файла
    private suspend fun loadJsonDataAsync(lessonNumber: Int): List<Word>? {
        val fileName = "arabic_for_russian/${String.format("%02d.json", lessonNumber)}"
        return withContext(Dispatchers.IO) {
            try {
                val assetManager = requireContext().assets
                // Проверка, существует ли файл
                val files = assetManager.list("arabic_for_russian") ?: emptyArray()
                if (!files.contains(String.format("%02d.json", lessonNumber))) {
                    Log.e("WordListFragment", "File $fileName not found in assets.")
                    return@withContext null
                }

                Log.d("WordListFragment", "Loading JSON file: $fileName")
                val json = assetManager.open(fileName).bufferedReader().use { it.readText() }
                val type = object : TypeToken<List<Word>>() {}.type
                val wordList: List<Word> = Gson().fromJson(json, type)
                Log.d("WordListFragment", "Successfully parsed JSON file: $fileName")
                wordList
            } catch (e: Exception) {
                Log.e("WordListFragment", "Error loading JSON file: $fileName", e)
                null
            }
        }
    }


    // Метод для создания MaterialCardView для каждого слова
    private fun createWordCard(context: Context, word: Word): MaterialCardView {
        return MaterialCardView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16.dpToPx(), 16.dpToPx(), 16.dpToPx(), 16.dpToPx())
            }
            radius = 16f
            setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
            elevation = 8f

            // Контейнер для отображения текста
            addView(LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(16, 16, 16, 16)

                // Добавляем перевод на русском слева
                addView(LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                    )
                    addView(createTextView(context, word.singular_ru))
                    addView(createTextView(context, word.plural_ru))
                })

                // Добавляем текст на арабском справа
                addView(LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                    )
                    gravity = Gravity.END
                    addView(createTextView(context, word.singular_ar, textAlignment = View.TEXT_ALIGNMENT_TEXT_END))
                    addView(createTextView(context, word.plural_ar, textAlignment = View.TEXT_ALIGNMENT_TEXT_END))
                })
            })
        }
    }

    // Метод для создания текстового представления
    private fun createTextView(context: Context, text: String, textAlignment: Int = View.TEXT_ALIGNMENT_TEXT_START): TextView {
        return TextView(context).apply {
            this.text = text
            this.textAlignment = textAlignment
            textSize = 25f
            setTextColor(ContextCompat.getColor(context, R.color.black))
        }
    }

    // Класс данных для представления JSON-объекта
    data class Word(
        val singular_ar: String,
        val plural_ar: String,
        val singular_ru: String,
        val plural_ru: String
    )

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}