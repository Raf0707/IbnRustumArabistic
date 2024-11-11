package ibn.rustum.arabistic.ui.words.arabic_for_russian.cards.training

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ibn.rustum.arabistic.R
import ibn.rustum.arabistic.databinding.FragmentTrainingModeBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.LinkedList
import java.util.Queue
import kotlin.random.Random

class TrainingModeFragment : Fragment() {

    private val wordsQueue: Queue<Pair<String, String>> = LinkedList()
    private lateinit var binding: FragmentTrainingModeBinding
    private var trainingMode: String? = null
    private val checkedArabicWords = mutableListOf<String>()
    private val checkedRussianWords = mutableListOf<String>()
    private val repeatCountMap = mutableMapOf<Pair<String, String>, Int>()
    private var currentWord: Pair<String, String>? = null
    private val usedWords = mutableSetOf<Pair<String, String>>() // Отслеживаем использованные слова

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrainingModeBinding.inflate(inflater, container, false)

        val jsonFilePath = arguments?.getString("jsonFilePath")
        trainingMode = arguments?.getString("trainingMode") // Получаем режим

        jsonFilePath?.let {
            try {
                val jsonData = context?.assets?.open(it)?.bufferedReader().use { reader -> reader?.readText() }
                jsonData?.let { data ->
                    val jsonArray = JSONArray(data) // Используем JSONArray вместо JSONObject

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)

                        // Получаем слова на арабском и русском из каждого объекта в массиве
                        val arabicWord = jsonObject.getString("ar")
                        val russianWord = jsonObject.getString("ru")

                        checkedArabicWords.add(arabicWord)
                        checkedRussianWords.add(russianWord)
                    }

                    Log.d("checkedArabicWords", checkedArabicWords.toString())
                    Log.d("checkedRussianWords", checkedRussianWords.toString())

                    // Используем переменную mode для настройки отображения или других действий
                    Log.d("TrainingMode", "Current mode: $trainingMode")
                    // Здесь можно добавить логику в зависимости от значения `mode`, если это требуется
                }
            } catch (e: IOException) {
                Log.e("TrainingModeFragment", "Error loading JSON from assets: ${e.message}")
            } catch (e: JSONException) {
                Log.e("TrainingModeFragment", "Error parsing JSON: ${e.message}")
            }
        }


        // Получаем текущее слово из textWord
        val currentText = binding.textWord.text.toString()

// Находим индекс текущего слова в списке checkedArabicWords
        val currentItemIndex = checkedArabicWords.indexOf(currentText)

// Проверяем, что индекс найден (не равен -1)
        if (currentItemIndex != -1) {
            val currentArabicWord = checkedArabicWords[currentItemIndex]
            val currentRussianWord = checkedRussianWords[currentItemIndex]

            binding.showAnswerSwitch.setOnCheckedChangeListener { _, isChecked ->
                // Если переключатель включен, показываем ответ
                if (isChecked) {
                    binding.answerWord.visibility = View.VISIBLE
                    val answer = when (trainingMode) {
                        "Ar-Ru" -> currentArabicWord // если режим тренировки "Ar-Ru", показываем слово на русском
                        "Ru-Ar" -> currentRussianWord // если режим тренировки "Ru-Ar", показываем слово на арабском
                        "Merge" -> if (binding.textWord.text == currentArabicWord) currentRussianWord else currentArabicWord // если текст совпадает с арабским словом, показываем русское, и наоборот
                        else -> currentRussianWord // по умолчанию показываем слово на русском
                    }
                    binding.answerWord.text = answer
                } else {
                    // Если переключатель выключен, скрываем ответ
                    binding.answerWord.visibility = View.INVISIBLE
                }
            }
        } else {
            Log.e("TrainingModeFragment", "Текущий текст не найден в списке checkedArabicWords: $currentText")
            // Здесь можно добавить альтернативное действие, если слово не найдено
        }



        initWordsQueue()
        displayNextWord()
        setupCardClick()
        setupCardLongClick()
        setupSwitchListener()

        return binding.root
    }

    private fun initWordsQueue() {
        val wordsList = checkedArabicWords.zip(checkedRussianWords)
        Log.d("TrainingMode", "Arabic Words List: $checkedArabicWords")
        Log.d("TrainingMode", "Russian Words List: $checkedRussianWords")
        Log.d("TrainingMode", "Words List: $wordsList") // Логируем список Pair
        // Для каждого слова задаем количество повторений
        wordsList.forEach { wordPair ->
            repeatCountMap[wordPair] = (45..100).random()
        }

        // Перемешиваем список и добавляем в очередь
        wordsQueue.addAll(wordsList.shuffled())
    }


    private fun displayNextWord() {
        // Выбираем следующее слово
        currentWord = getNextWord() ?: return

        // Выводим слово в зависимости от выбранного режима
        val displayedWord = when (trainingMode) {
            "Ar-Ru" -> currentWord?.first
            "Ru-Ar" -> currentWord?.second
            "Merge" -> if (Random.nextBoolean()) currentWord?.first else currentWord?.second
            else -> currentWord?.first
        } ?: ""

        binding.textWord.text = displayedWord

        // Если свитч включен, сразу показываем ответ для нового слова
        if (binding.showAnswerSwitch.isChecked) {
            showAnswer()
        } else {
            binding.answerWord.visibility = View.INVISIBLE
        }
    }

    private fun showAnswer() {
        val answer = when (trainingMode) {
            "Ar-Ru" -> currentWord?.second
            "Ru-Ar" -> currentWord?.first
            "Merge" -> if (binding.textWord.text == currentWord?.first) currentWord?.second else currentWord?.first
            else -> ""
        }
        binding.answerWord.text = answer
        binding.answerWord.visibility = View.VISIBLE
    }


    // Функция получения следующего слова из очереди с учетом частоты повторений
    private fun getNextWord(): Pair<String, String>? {
        val availableWords = wordsQueue.filterNot { usedWords.contains(it) }

        if (availableWords.isEmpty()) {
            // Если все слова использованы, перезапускаем очередь
            usedWords.clear()

            // Преобразуем очередь в список, перемешиваем и возвращаем в очередь
            val shuffledWords = wordsQueue.toMutableList().shuffled()
            wordsQueue.clear()
            wordsQueue.addAll(shuffledWords)
        }

        // Случайным образом выбираем слово
        return wordsQueue.poll().also {
            it?.let { usedWords.add(it) }
        }
    }

    private suspend fun loadJsonDataAsync(lessonNumber: Int): String? {
        return try {
            val fileName = "arabic_for_russian/cards/${String.format("%02d.json", lessonNumber)}"
            context?.assets?.open(fileName)?.bufferedReader().use { it?.readText() }
        } catch (e: IOException) {
            Log.e("FileLoading", "Error loading JSON file: $e")
            null
        }
    }


    private fun setupCardClick() {
        binding.cardWord.setOnClickListener {
            // Поворот карточки и отображение перевода
            val answer = when (trainingMode) {
                "Ar-Ru" -> currentWord?.second
                "Ru-Ar" -> currentWord?.first
                "Merge" -> if (binding.textWord.text == currentWord?.first) currentWord?.second else currentWord?.first
                else -> ""
            }

            flipCard(answer)
        }
    }

    private fun setupCardLongClick() {
        binding.cardWord.setOnLongClickListener {
            val answer = when (trainingMode) {
                "Ar-Ru" -> currentWord?.second
                "Ru-Ar" -> currentWord?.first
                "Merge" -> if (binding.textWord.text == currentWord?.first) currentWord?.second else currentWord?.first
                else -> ""
            }

            // Показать правильный ответ
            flipCard(answer)
            true
        }
    }

    private fun setupSwitchListener() {
        binding.showAnswerSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Если переключатель включен, показываем ответ
            if (isChecked) {
                binding.answerWord.visibility = View.VISIBLE
                val answer = when (trainingMode) {
                    "Ar-Ru" -> currentWord?.second
                    "Ru-Ar" -> currentWord?.first
                    "Merge" -> if (binding.textWord.text == currentWord?.first) currentWord?.second else currentWord?.first
                    else -> ""
                }
                binding.answerWord.text = answer
            } else {
                // Если переключатель выключен, скрываем ответ
                binding.answerWord.visibility = View.INVISIBLE
            }
        }
    }

    private fun flipCard(answer: String?) {
        // Временно скрываем текст
        binding.textWord.visibility = View.INVISIBLE

        // Анимация поворота карточки
        binding.cardWord
            .animate()
            .scaleX(0.8f)
            .scaleY(0.8f)
            .rotationY(90f)
            .setDuration(150)
            .withEndAction {

            binding.textWord.visibility = View.VISIBLE
            binding.cardWord.rotationY = -90f
            binding.cardWord.animate()
                .scaleX(1f)
                .scaleY(1f)
                .rotationY(0f)
                .setDuration(150)
                .start()

            /*Handler(Looper.getMainLooper()).postDelayed({
                // Показать следующее слово
                displayNextWord()
            }, 150)*/
                displayNextWord()
        }.start()
    }
}
