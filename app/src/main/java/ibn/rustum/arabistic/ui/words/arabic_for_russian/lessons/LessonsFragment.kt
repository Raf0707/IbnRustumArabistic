package ibn.rustum.arabistic.ui.words.arabic_for_russian.lessons

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors
import ibn.rustum.arabistic.R

class LessonsFragment : Fragment() {

    private var currentTheme: Int = AppCompatDelegate.getDefaultNightMode()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val context = requireContext()

        val scrollView = ScrollView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(0, 25.dpToPx(), 0, 100.dpToPx())
            clipToPadding = false
        }

        val rootLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val lessonTitles = listOf(
            "Арабский алфавит", "Цифры", "Личные местоимения", "Указательные местоимения",
            "Относительные местоимения", "Вопросительные местоимения", "Часто используемые слова",
            "Приветствие", "Знакомство", "Просьбы", "Поздравление и соболезнование",
            "Повеления и Запреты", "Семья", "Дни недели", "Месяцы", "Времена года",
            "Временные промежутки дня", "Время", "Погода", "Цвета", "Купля-Продажа", "Экономика",
            "Одежда", "В доме", "Тело человека", "Органы чувств человека", "Описание человека",
            "Описание вещей", "Чувства и ощущения", "Еда и питье", "Кафе", "Здоровье и болезнь",
            "Образование", "Стороны света", "Кратность и дроби", "Вычисления", "Полезные ископаемые",
            "Весы и меры", "Спорт", "Профессии", "Путешествие", "Транспортные услуги", "Гостиница",
            "Осмотр", "Музей", "Природа", "Животные", "Хобби", "Инструменты и приборы", "Связь",
            "Религии и вероубеждения", "Намаз", "Хадж", "Движение", "Армия", "Народы и нации"
        )

        lessonTitles.forEachIndexed { index, title ->
            val cardView = createMaterialCardView(context, "Урок ${index + 1}: $title", index)
            val marginTop = if (index == 0) 25 else 5
            (cardView.layoutParams as ViewGroup.MarginLayoutParams).topMargin = marginTop.dpToPx()
            rootLayout.addView(cardView)
        }

        scrollView.addView(rootLayout)
        return scrollView
    }

    private fun createMaterialCardView(context: Context, text: String, index: Int): MaterialCardView {
        val cardView = MaterialCardView(context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(5.dpToPx(), 0, 5.dpToPx(), 5.dpToPx())
            }
            radius = 16f
            strokeWidth = 3
            setCardBackgroundColor(getCardBackgroundColor(context))
            setContentPadding(32, 32, 32, 32)
            elevation = 8f
        }

        val textView = TextView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.LEFT or Gravity.BOTTOM
            this.text = text
            textSize = 25f
            setTextColor(getTextColor(context))
        }

        cardView.addView(textView)
        cardView.setOnClickListener {
            val fileName = String.format("lesson_%02d.json", index + 1)
            val action = LessonsFragmentDirections.actionLessonsFragmentToWordListFragment(fileName)
            findNavController().navigate(action)
        }
        return cardView
    }

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


    private fun isDarkTheme(context: Context): Boolean {
        val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}
