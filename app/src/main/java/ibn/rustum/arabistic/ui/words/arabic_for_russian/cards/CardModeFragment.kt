package ibn.rustum.arabistic.ui.words.arabic_for_russian.cards

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ibn.rustum.arabistic.R
import ibn.rustum.arabistic.adapters.CardAdapter
import ibn.rustum.arabistic.databinding.FragmentCardModeBinding

class CardModeFragment : Fragment() {

    lateinit var binding: FragmentCardModeBinding
    public var isAllSelected = false

    private val checkedArabicWords = mutableListOf<String>()
    private val checkedRussianWords = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding = FragmentCardModeBinding.inflate(layoutInflater, container, false)

        // Retrieve the arguments as arrays
        val arabicWordsArray = arguments?.getStringArray("setOfArabicWords") ?: arrayOf()
        val translateWordsArray = arguments?.getStringArray("setOfTranslateWords") ?: arrayOf()

        Log.d("arabicWordsArray", arabicWordsArray.forEach { println(it) }.toString())
        Log.d("translateWordsArray", translateWordsArray.forEach { println(it) }.toString())

        // Convert arrays back to sets if needed
        val setOfArabicWords = arabicWordsArray.toMutableSet()
        val setOfTranslateWords = translateWordsArray.toMutableSet()

        binding.buttonTraining.setOnClickListener { showModeSelectionPopup(isTraining = true) }
        binding.buttonTest.setOnClickListener { showModeSelectionPopup(isTraining = false) }

        // Set up toolbar menu
        binding.toolbar.inflateMenu(R.menu.training_menu)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_select_all -> {
                    when(isAllSelected) {
                        false -> {
                            selectAllCards()
                            /*binding.toolbar.menu.getItem(R.id.action_select_all)
                                .title = "Очистить все"*/
                            item.title = "Очистить все"
                        }
                        else -> {
                            unselectAllCards()
                            /*binding.toolbar.menu.getItem(R.id.action_select_all)
                                .title = "Выбрать все"*/
                            item.title = "Выбрать все"
                        }
                    }

                }
                R.id.action_training -> {
                    showModeSelectionPopup(isTraining = true)
                    true
                }
                R.id.action_test -> {
                    showModeSelectionPopup(isTraining = false)
                    true
                }
                R.id.action_close -> requireActivity().onBackPressed()
                else -> false
            }
            true
        }

        setupCardList()
        setupBottomButtons()

        binding.buttonTraining.setOnClickListener { showModeSelectionPopup(isTraining = true) }
        binding.buttonTest.setOnClickListener { showModeSelectionPopup(isTraining = false) }


        return binding.root
    }

    private fun updateCheckedWords(wordArabic: String, wordRussian: String, isChecked: Boolean) {
        if (isChecked) {
            checkedArabicWords.add(wordArabic)
            checkedRussianWords.add(wordRussian)
        } else {
            checkedArabicWords.remove(wordArabic)
            checkedRussianWords.remove(wordRussian)
        }
    }

    private fun showModeSelectionPopup(isTraining: Boolean) {
        if (checkedArabicWords.size < 5 || checkedRussianWords.size < 5) {
            Snackbar.make(binding.root, "Выберите не менее 5 слов", Snackbar.LENGTH_SHORT).show()
            return
        }

        val popupMenu = PopupMenu(requireContext(), if (isTraining) binding.buttonTraining else binding.buttonTest)
        popupMenu.menu.apply {
            add("Арабский -> Русский")
            add("Русский -> Арабский")
            add("В перемешку")
        }
        popupMenu.setOnMenuItemClickListener { menuItem ->
            val mode = when (menuItem.title) {
                "Арабский -> Русский" -> "Ar-Ru"
                "Русский -> Арабский" -> "Ru-Ar"
                "В перемешку" -> "Merge"
                else -> ""
            }
            startMode(isTraining, mode)
            true
        }
        popupMenu.show()
    }

    private fun startMode(isTraining: Boolean, mode: String) {
        val action = if (isTraining) {
            CardModeFragmentDirections.actionCardModeFragmentToTrainingModeFragment(
                checkedArabicWords.toTypedArray(),
                checkedRussianWords.toTypedArray(),
                mode
            )
        } else {
            CardModeFragmentDirections.actionCardModeFragmentToTestModeFragment(
                checkedArabicWords.toTypedArray(),
                checkedRussianWords.toTypedArray(),
                mode
            )
        }
        findNavController().navigate(action)
    }

    private fun setupCardList() {
        val arabicWordsArray = arguments?.getStringArray("setOfArabicWords") ?: arrayOf()
        val translateWordsArray = arguments?.getStringArray("setOfTranslateWords") ?: arrayOf()
        val items = arabicWordsArray.zip(translateWordsArray)

        val cardAdapter = CardAdapter(items) { arabicWord, translatedWord, isChecked ->
            updateCheckedWords(arabicWord, translatedWord, isChecked)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cardAdapter
        }
    }

    private fun setupBottomButtons() {
        binding.buttonTraining.setOnClickListener {
            startTrainingMode()
        }

        binding.buttonTest.setOnClickListener {
            startTestMode()
        }
    }

    private fun startTrainingMode() {
        // Logic to start training with selected cards
    }

    private fun startTestMode() {
        // Logic to start test with selected cards
    }

    private fun selectAllCards() {
        val adapter = binding.recyclerView.adapter as? CardAdapter
        adapter?.selectAll()
        isAllSelected = true
    }

    private fun unselectAllCards() {
        val adapter = binding.recyclerView.adapter as? CardAdapter
        adapter?.unselectAll()
        isAllSelected = false
    }

}