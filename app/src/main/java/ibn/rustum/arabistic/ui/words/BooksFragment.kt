package ibn.rustum.arabistic.ui.words

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import ibn.rustum.arabistic.R
import ibn.rustum.arabistic.databinding.FragmentBooksBinding

class BooksFragment : Fragment() {

    private lateinit var binding: FragmentBooksBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Инициализация binding
        binding = FragmentBooksBinding.inflate(inflater, container, false)

        // Создаем корневой контейнер
        val rootLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(getBackgroundColor(requireContext())) // Устанавливаем цвет фона в зависимости от темы
            gravity = Gravity.CENTER
        }

        // Создаем текст для каждого CardView
        val titles = listOf(
            "Арабский язык для говорящих по русски",
            "Дурус Аш-Шифахия",
            "Мабдауль Кыраат"
        )

        titles.forEach { title ->
            val cardView = createMaterialCardView(title, 24)
            // Устанавливаем onClickListener для конкретной карточки
            cardView.setOnClickListener {
                when (title) {
                    "Арабский язык для говорящих по русски" -> findNavController().navigate(R.id.action_booksFragment_to_lessonsFragment)
                }
            }

            rootLayout.addView(cardView)
        }

        binding.root.addView(rootLayout)
        return binding.root
    }

    private fun createMaterialCardView(text: String, topMargin: Int): MaterialCardView {
        val isDarkTheme = isDarkTheme(requireContext())
        val cardBackgroundColor = if (isDarkTheme) {
            R.color.md_theme_dark_surfaceVariant
        } else {
            R.color.md_theme_light_surfaceVariant
        }

        val textColor = if (isDarkTheme) {
            R.color.md_theme_dark_onSurface
        } else {
            R.color.md_theme_light_onSurface
        }

        val cardView = MaterialCardView(requireContext()).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                400  // Увеличенная высота
            ).apply {
                setMargins(24, topMargin, 24, 24)  // Верхний отступ зависит от аргумента topMargin
            }
            setCardBackgroundColor(ContextCompat.getColor(requireContext(), cardBackgroundColor)) // Устанавливаем цвет карточки
            radius = 24f
            elevation = 12f
            setPadding(48, 48, 48, 48)  // Увеличенный padding внутри карточки
        }

        val textView = TextView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT  // Растягиваем TextView на всю высоту CardView
            )
            gravity = Gravity.CENTER  // Центрируем текст как по горизонтали, так и по вертикали
            this.text = text
            textSize = 24f  // Увеличенный размер текста
            setTextColor(ContextCompat.getColor(requireContext(), textColor)) // Устанавливаем цвет текста
        }

        cardView.addView(textView)
        return cardView
    }

    private fun getBackgroundColor(context: Context): Int {
        return if (isDarkTheme(context)) {
            ContextCompat.getColor(context, R.color.md_theme_dark_background)
        } else {
            ContextCompat.getColor(context, R.color.md_theme_light_background)
        }
    }

    private fun isDarkTheme(context: Context): Boolean {
        return (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }
}
