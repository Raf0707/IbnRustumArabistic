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
import org.json.JSONObject
import org.json.JSONArray
import java.io.File

class CardModeFragment : Fragment() {

    lateinit var binding: FragmentCardModeBinding
    var isAllSelected = false
    lateinit var fileArabicWords: String

    private val checkedArabicWords = mutableListOf<String>()
    private val checkedRussianWords = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardModeBinding.inflate(layoutInflater, container, false)

        // Retrieve the arguments as arrays
        //val arabicWordsArray = arguments?.getStringArray("setOfArabicWords") ?: arrayOf()
        //val translateWordsArray = arguments?.getStringArray("setOfTranslateWords") ?: arrayOf()

        // Convert arrays back to sets if needed
        //val setOfArabicWords = arabicWordsArray.toMutableSet()
        //val setOfTranslateWords = translateWordsArray.toMutableSet()

        fileArabicWords = arguments?.getString("arabicWords") ?: ""

        binding.buttonTraining.setOnClickListener { showModeSelectionPopup(isTraining = true) }
        binding.buttonTest.setOnClickListener { showModeSelectionPopup(isTraining = false) }

        // Set up toolbar menu
        /*binding.toolbar.inflateMenu(R.menu.training_menu)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_select_all -> {
                    if (!isAllSelected) {
                        selectAllCards()
                        item.title = "Очистить все"
                    } else {
                        unselectAllCards()
                        item.title = "Выбрать все"
                    }
                }
                R.id.action_training -> {
                    showModeSelectionPopup(isTraining = true)
                }
                R.id.action_test -> {
                    showModeSelectionPopup(isTraining = false)
                }
                R.id.action_close -> requireActivity().onBackPressed()
            }
            true
        }*/

        setupCardList()
        setupBottomButtons()

        return binding.root
    }

    /*private fun updateCheckedWords(wordArabic: String, wordRussian: String, isChecked: Boolean) {
        if (isChecked) {
            checkedArabicWords.add(wordArabic)
            checkedRussianWords.add(wordRussian)
        } else {
            checkedArabicWords.remove(wordArabic)
            checkedRussianWords.remove(wordRussian)
        }
    }*/

    private fun showModeSelectionPopup(isTraining: Boolean) {
        /*if (checkedArabicWords.size < 5 || checkedRussianWords.size < 5) {
            Snackbar.make(binding.root, "Выберите не менее 5 слов", Snackbar.LENGTH_SHORT).show()
            return
        }*/

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
        //val jsonFilePath = createSelectedWordsJsonFile() ?: return
        val jsonFilePath = fileArabicWords

        val bundle = Bundle().apply {
            putString("jsonFilePath", jsonFilePath)
            putString("trainingMode", mode)
        }

        val destination = if (isTraining) {
            R.id.trainingModeFragment
        } else {
            R.id.testModeFragment
        }

        findNavController().navigate(destination, bundle)
    }

    /*private fun createSelectedWordsJsonFile(): String? {
        return try {
            // Создаем JSON-объект для хранения выделенных слов
            val jsonObject = JSONObject().apply {
                put("checkedArabicWords", JSONArray(checkedArabicWords))
                put("checkedRussianWords", JSONArray(checkedRussianWords))
            }

            // Сохраняем JSON в файл
            val jsonFile = File(requireContext().cacheDir, "selected_words.json")
            jsonFile.writeText(jsonObject.toString())

            jsonFile.absolutePath // Возвращаем путь к файлу
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }*/

    /*private fun setupCardList() {
        val arabicWordsArray = arguments?.getStringArray("setOfArabicWords") ?: arrayOf()
        val translateWordsArray = arguments?.getStringArray("setOfTranslateWords") ?: arrayOf()
        val items = arabicWordsArray.zip(translateWordsArray)

        val cardAdapter = CardAdapter(items) /*{ arabicWord, translatedWord, isChecked ->
            updateCheckedWords(arabicWord, translatedWord, isChecked)
        }*/

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cardAdapter
        }
    }*/

    private fun setupCardList() {
        // Получение пути к JSON-файлу из аргументов
        val fileArabicWordsPath = arguments?.getString("arabicWords") ?: ""

        // Проверка, что путь не пуст
        if (fileArabicWordsPath.isEmpty()) {
            Log.e("setupCardList", "File path is empty")
            return
        }

        try {
            // Загрузка содержимого JSON-файла из assets
            val jsonFileContent = requireContext().assets.open(fileArabicWordsPath).bufferedReader().use { it.readText() }

            // Парсинг JSON-строки в список пар слов
            val items = mutableListOf<Pair<String, String>>()
            val jsonArray = JSONArray(jsonFileContent)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val arabicWord = jsonObject.getString("ar")
                val translatedWord = jsonObject.getString("ru")
                items.add(arabicWord to translatedWord)
            }

            // Создание адаптера для RecyclerView
            val cardAdapter = CardAdapter(items)

            // Настройка RecyclerView
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = cardAdapter
            }
        } catch (e: Exception) {
            Log.e("setupCardList", "Error loading or parsing JSON file", e)
        }
    }

    private fun setupBottomButtons() {
        binding.buttonTraining.setOnClickListener {
            showModeSelectionPopup(isTraining = true)
        }

        binding.buttonTest.setOnClickListener {
            showModeSelectionPopup(isTraining = false)
        }
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
