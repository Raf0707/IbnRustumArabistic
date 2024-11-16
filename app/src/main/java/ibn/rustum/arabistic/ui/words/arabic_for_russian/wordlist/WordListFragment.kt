package ibn.rustum.arabistic.ui.words.arabic_for_russian.wordlist


import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ibn.rustum.arabistic.R
import ibn.rustum.arabistic.util.CustomTabUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class WordListFragment : Fragment() {

    private var overlay: FrameLayout? = null

    lateinit var lessonNumPublic: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = requireContext()
        val lessonNumber = arguments?.getInt("lesson_number") ?: 1
        lessonNumPublic = lessonNumber.toString()
        Log.d("PUBLIC LESSON", lessonNumPublic)
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
            val jsonData = loadJsonDataAsync(lessonNumber + 1)
            if (jsonData != null) {
                Log.d("WordListFragment", "Data loaded successfully, adding cards")
                jsonData.forEach { item ->
                    val cardView = createWordCard(context, item)
                    linearLayout.addView(cardView)
                    //Log.d("WordListFragment", "Added card for word: ${item.singular_ru}")
                }

                // Добавляем последнюю карточку
                val finalCard = createFinalCard(context)
                linearLayout.addView(finalCard)

            } else {
                Log.e("WordListFragment", "Failed to load data for lesson $lessonNumber")
            }
        }

        // Создаем контейнерный FrameLayout для хранения overlay и основного контента
        val frameLayout = FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Добавляем linearLayout внутрь NestedScrollView
        val scrollView = NestedScrollView(context).apply {
            setPadding(0, 30.dpToPx(), 0, 100.dpToPx())
            addView(linearLayout)  // ScrollView может содержать только один дочерний элемент
        }

        // Добавляем NestedScrollView как единственный дочерний элемент в frameLayout
        frameLayout.addView(scrollView)

        return frameLayout
    }

    private fun showWordDialog(context: Context, word: Word) {
        val dialog = Dialog(context).apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#B3000000")))
        }

        val dialogCard = createWordCard(context, word).apply {
            // Для карточки с singular
            val wordCard = createSingleWordCard(context, word.ar, word.ru)
            wordCard.setOnClickListener {
                showPopupMenu(this, word.ar, word.ru)
            }


            // Добавляем обе карточки в основной layout
            addView(wordCard)

        }

        val container = FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            val dialogCardLayoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
            addView(dialogCard, dialogCardLayoutParams)
        }

        dialog.setContentView(container)
        dialog.show()
    }

    // Модификация функции для отображения меню
    private fun showPopupMenu(anchor: View, arabicWord: String, translatedWord: String) {
        PopupMenu(requireContext(), anchor).apply {
            // Элемент меню "Поиск по слову (арабский)"
            menu.add("Поиск по слову (арабский)").setOnMenuItemClickListener {
                /*val action = WordListFragmentDirections.actionWordListFragmentToSearchWordFragment(arabicWord)

                // Логируем переданную ссылку для арабского слова
                Log.d("WordListFragment", "Navigating to SearchWordFragment with arabicWord: $arabicWord")
                Log.d("WordListFragment", "http://arabus.ru/search/$arabicWord")

                findNavController().navigate(action)*/
                    v -> (
                    CustomTabUtil()
                        .openCustomTab(
                            activity,
                            "http://arabus.ru/search/$arabicWord",
                            R.color.purple_300
                        )
                    )
                true
            }

            // Элемент меню "Поиск по слову (русский)"
            menu.add("Поиск по слову (русский)").setOnMenuItemClickListener {
                /*val action = WordListFragmentDirections.actionWordListFragmentToSearchWordFragment(translatedWord)

                // Логируем переданную ссылку для переведенного слова
                Log.d("WordListFragment", "Navigating to SearchWordFragment with translatedWord: $translatedWord")
                Log.d("WordListFragment", "http://arabus.ru/search/$translatedWord")
                findNavController().navigate(action)*/
                    v -> (
                    CustomTabUtil()
                        .openCustomTab(
                            activity,
                            "http://arabus.ru/search/$translatedWord",
                            R.color.purple_300
                        )
                    )
                true
            }

            // Элемент меню "Перейти в счетчик"
            menu.add("Перейти в счетчик").setOnMenuItemClickListener {
                //TODO Добавить передачу слова с переводом и цель 200
                //Snackbar.make(anchor, "Перейти в счетчик", Snackbar.LENGTH_SHORT).show()
                val combinedWord = "$arabicWord - $translatedWord"
                val action = WordListFragmentDirections.actionWordListFragmentToMainSwipeFragment(
                    combinedWord, 200
                )
                findNavController().navigate(action)
                true
            }

            // Показываем PopupMenu
            show()
        }
    }



    private suspend fun loadJsonDataAsync(lessonNumber: Int): List<Word>? {
        val fileName = "arabic_for_russian/wordlist/${String.format("%02d.json", lessonNumber)}"
        return withContext(Dispatchers.IO) {
            try {
                val assetManager = requireContext().assets
                val files = assetManager.list("arabic_for_russian/wordlist") ?: emptyArray()
                if (!files.contains(String.format("%02d.json", lessonNumber))) {
                    Log.e("WordListFragment", "File $fileName not found in assets.")
                    return@withContext null
                }

                val json = assetManager.open(fileName).bufferedReader().use { it.readText() }
                val type = object : TypeToken<List<Word>>() {}.type
                Gson().fromJson<List<Word>>(json, type)
            } catch (e: Exception) {
                Log.e("WordListFragment", "Error loading JSON file: $fileName", e)
                null
            }
        }
    }

    // Функция для создания карточек для singular и plural
    private fun createWordCard(context: Context, word: Word): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16.dpToPx(), 8.dpToPx(), 16.dpToPx(), 8.dpToPx())
            }

            addView(createSingleWordCard(context, word.ar, word.ru))

            /*
            // Добавляем карточку для singular
            addView(createSingleWordCard(context, word.singular_ar, word.singular_ru, "singular"))

            // Добавляем карточку для plural
            addView(createSingleWordCard(context, word.plural_ar, word.plural_ru, "plural"))
            */
        }
    }

    // Функция для создания карточки и добавления клика на неё
    private fun createSingleWordCard(
        context: Context,
        arabicWord: String,
        russianWord: String,
    ): MaterialCardView {
        return MaterialCardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8.dpToPx(), 8.dpToPx(), 8.dpToPx(), 8.dpToPx())
            }
            radius = 12f
            //setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
            setCardBackgroundColor(getCardBackgroundColor(context))
            elevation = 4f
            setPadding(16.dpToPx(), 16.dpToPx(), 16.dpToPx(), 16.dpToPx())

            addView(LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER

                addView(TextView(context).apply {
                    text = arabicWord
                    textSize = 28f
                    setTextColor(getTextColor(context))
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    typeface = Typeface.DEFAULT_BOLD
                })

                addView(TextView(context).apply {
                    text = russianWord
                    textSize = 18f
                    setTextColor(getSubColor(context))
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    setPadding(0, 8.dpToPx(), 0, 0)
                })
            })

            // Передаем данные для singular или plural в onClickListener
            setOnClickListener {
                // В зависимости от того, кликнули по singular или plural, передаем соответствующие данные
                showPopupMenu(this, arabicWord, russianWord)
            }
        }
    }

    private fun showOverlay(context: Context) {
        if (overlay == null) {
            overlay = FrameLayout(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                setBackgroundColor(Color.parseColor("#80000000"))
                setOnClickListener { removeOverlay() }
            }
            (view as? ViewGroup)?.findViewById<FrameLayout>(R.id.overlay_container)?.addView(overlay)
        }
    }

    private fun removeOverlay() {
        overlay?.let {
            (view as? ViewGroup)?.findViewById<FrameLayout>(R.id.overlay_container)?.removeView(it)
            overlay = null
        }
    }

    private fun createFinalCard(context: Context): MaterialCardView {
        return MaterialCardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                150.dpToPx()
            ).apply {
                setMargins(16.dpToPx(), 16.dpToPx(), 16.dpToPx(), 16.dpToPx())
            }
            setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorGreenIce))
            radius = 16f
            elevation = 8f

            addView(TextView(context).apply {
                text = "Карточки"
                textSize = 20f
                setTextColor(ContextCompat.getColor(context, R.color.black))
                gravity = Gravity.CENTER
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            })

            setOnClickListener {
                // Initialize the sets
                val setOfArabicWords: MutableSet<String> = mutableSetOf()
                val setOfTranslateWords: MutableSet<String> = mutableSetOf()

                // Get the lesson number from arguments
                val lessonNumber = arguments?.getInt("lesson_number") ?: 1

                lifecycleScope.launch {
                    // Load JSON data for the given lesson number
                    val jsonData = loadJsonDataAsync(lessonNumber + 1)
                    if (jsonData != null) {
                        jsonData.forEach { item ->
                            // Populate the sets with Arabic and translation words
                            setOfArabicWords.add(item.ar)
                            setOfArabicWords.add(item.ru)
                            //setOfTranslateWords.add(item.singular_ru)
                            //setOfTranslateWords.add(item.plural_ru)
                        }

                        // Convert sets to arrays for navigation arguments
                        //val arabicWordsArray = setOfArabicWords.toTypedArray()
                        //val translateWordsArray = setOfTranslateWords.toTypedArray()

                        // Use SafeArgs to navigate and pass the arrays
                        val directions = WordListFragmentDirections
                            .actionWordListFragmentToCardModeFragment(
                                arabicWords = "arabic_for_russian/wordlist/${String.format("%02d.json", lessonNumber + 1)}"
                            )

                        Log.d("FILENAME", "arabic_for_russian/wordlist/${String.format("%02d.json", lessonNumber + 1)}")

                        findNavController().navigate(directions)
                    } else {
                        Log.e("WordListFragment", "Failed to load data for lesson ${lessonNumber + 1}")
                    }
                }
            }


        }
    }

    /*data class Word(
        val singular_ar: String,
        val plural_ar: String,
        val singular_ru: String,
        val plural_ru: String
    )*/

    data class Word(
        val ar: String,
        val ru: String
    )

    private fun getCardBackgroundColor(context: Context): Int {
        val isDarkTheme = isDarkTheme(context)
        return if (isDarkTheme) {
            // Используем цвет из темной темы
            ContextCompat.getColor(context, R.color.md_theme_dark_surfaceVariant)
        } else {
            // Используем цвет из светлой темы
            ContextCompat.getColor(context, R.color.md_theme_light_surfaceVariant)
        }
    }

    private fun getTextColor(context: Context): Int {
        val isDarkTheme = isDarkTheme(context)
        return if (isDarkTheme) {
            // Используем цвет текста из темной темы
            ContextCompat.getColor(context, R.color.md_theme_dark_onSurface)
        } else {
            // Используем цвет текста из светлой темы
            ContextCompat.getColor(context, R.color.md_theme_light_onSurface)
        }
    }

    private fun getSubColor(context: Context): Int {
        val isDarkTheme = isDarkTheme(context)
        return if (isDarkTheme) {
            // Используем цвет из темной темы
            ContextCompat.getColor(context, R.color.md_theme_dark_outline)
        } else {
            // Используем цвет из светлой темы
            ContextCompat.getColor(context, R.color.md_theme_light_outline)
        }
    }



    private fun isDarkTheme(context: Context): Boolean {
        val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}