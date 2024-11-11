package ibn.rustum.arabistic.ui.words.arabic_for_russian.lessons

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView

class LessonsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val scrollView = ScrollView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(0, 25.dpToPx(), 0, 100.dpToPx()) // Padding сверху и снизу
            clipToPadding = false
        }

        val rootLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        // Список названий для уроков
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

        // Создание карточек уроков
        lessonTitles.forEachIndexed { index, title ->
            val cardView = createMaterialCardView("Урок ${index + 1}: $title", index)
            // Устанавливаем отступ сверху для первой карточки и добавляем карточку в макет
            val marginTop = if (index == 0) 25 else 5
            (cardView.layoutParams as ViewGroup.MarginLayoutParams).topMargin = marginTop.dpToPx()
            rootLayout.addView(cardView)
        }

        scrollView.addView(rootLayout)
        return scrollView
    }

    // Создание MaterialCardView для урока
    private fun createMaterialCardView(text: String, index: Int): MaterialCardView {
        val cardView = MaterialCardView(requireContext()).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(5.dpToPx(), 0, 5.dpToPx(), 5.dpToPx())
            }
            radius = 16f
            strokeWidth = 3
            setCardBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            setContentPadding(32, 32, 32, 32)
            elevation = 8f
        }

        val textView = TextView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.LEFT or Gravity.BOTTOM
            this.text = text
            textSize = 25f
            setTextColor(Color.BLACK)
        }

        cardView.addView(textView)

        // Добавляем обработчик клика для навигации с передачей имени файла
        cardView.setOnClickListener {
            // Формируем имя файла с двумя цифрами
            val fileName = String.format("lesson_%02d.json", index + 1)

            // Логируем имя файла, которое мы отправляем
            Log.d("FILENAME", "$fileName")

            // Переход к WordListFragment с передачей имени файла
            val action = LessonsFragmentDirections.actionLessonsFragmentToWordListFragment(fileName)

            // Логируем информацию о навигации
            Log.d("LessonsFragment", "Navigating to WordListFragment with action: $action")

            findNavController().navigate(action)
        }



        return cardView
    }

    // Конвертация dp в px
    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}
