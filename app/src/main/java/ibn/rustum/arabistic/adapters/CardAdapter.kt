package ibn.rustum.arabistic.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ibn.rustum.arabistic.databinding.WordLinearItemBinding

class CardAdapter(
    private val items: List<Pair<String, String>>,
    private val onCardSelected: (String, String, Boolean) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    private val selectedItems = mutableSetOf<Int>()

    inner class CardViewHolder(val binding: WordLinearItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = WordLinearItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val (arabicWord, translatedWord) = items[position]

        with(holder.binding) {
            textArabicWord.text = arabicWord
            textRussianWord.text = translatedWord

            checkboxSelect.isChecked = selectedItems.contains(position)
            checkboxSelect.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectedItems.add(position) else selectedItems.remove(position)
                onCardSelected(arabicWord, translatedWord, isChecked)
            }
        }
    }

    override fun getItemCount() = items.size

    fun selectAll() {
        selectedItems.clear()
        items.indices.forEach { selectedItems.add(it) }
        notifyDataSetChanged()
    }

    fun unselectAll() {
        selectedItems.clear()
        notifyDataSetChanged()
    }
}
