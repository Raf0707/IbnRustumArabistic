package ibn.rustum.arabistic.ui.words.arabic_for_russian

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ibn.rustum.arabistic.R
import ibn.rustum.arabistic.domain.models.Word

private val selectedWords = mutableSetOf<Word>()

class WordAdapter(
    private val context: Context,
    private val words: List<Word>,
    private val onItemClick: ((Word, String) -> Unit?)?
) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val cardView = createWordCard(context)
        return WordViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = words[position]
        holder.bind(word, selectedWords.contains(word))
    }

    override fun getItemCount(): Int = words.size

    // Метод для получения позиции элемента по объекту Word
    fun getPositionForWord(word: Word): Int {
        return words.indexOf(word)  // Возвращаем индекс или -1, если не найден
    }

    inner class WordViewHolder(private val cardView: CardView) : RecyclerView.ViewHolder(cardView) {
        fun bind(word: Word, isSelected: Boolean) {
            // Настраиваем содержимое карточки
            val contentLayout = cardView.getChildAt(0) as LinearLayout
            val arabicTextView = contentLayout.getChildAt(0) as TextView
            val russianTextView = contentLayout.getChildAt(1) as TextView

            arabicTextView.text = word.singular_ar
            russianTextView.text = word.singular_ru

            // Установка клика для карточки
            cardView.setOnClickListener {
                onItemClick?.let { it1 -> it1(word, "singular") }
            }

            cardView.setCardBackgroundColor(
                if (isSelected) Color.LTGRAY else Color.WHITE
            )
        }
    }

    // Функция для создания карточки программно
    private fun createWordCard(context: Context): CardView {
        return CardView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                val margin = 16.dpToPx()
                //setMargins(margin, margin, margin, margin)
            }
            radius = 12f
            setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
            cardElevation = 8f

            addView(LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(16.dpToPx(), 16.dpToPx(), 16.dpToPx(), 16.dpToPx())

                addView(TextView(context).apply {
                    textSize = 28f
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                    typeface = Typeface.DEFAULT_BOLD
                    gravity = Gravity.CENTER
                })

                addView(TextView(context).apply {
                    textSize = 18f
                    setTextColor(ContextCompat.getColor(context, R.color.grey))
                    gravity = Gravity.CENTER
                })
            })
        }
    }

    // Update selection state
    fun updateSelection(word: Word, isSelected: Boolean) {
        if (isSelected) {
            selectedWords.add(word)
        } else {
            selectedWords.remove(word)
        }
        notifyDataSetChanged() // Обновляем список
    }

    private fun Int.dpToPx(): Int = (this * context.resources.displayMetrics.density).toInt()
}
