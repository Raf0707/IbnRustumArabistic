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

        arguments?.let {
            checkedArabicWords.addAll(it.getStringArray("checkedArabicWords")?.toList() ?: emptyList())
            checkedRussianWords.addAll(it.getStringArray("checkedRussianWords")?.toList() ?: emptyList())
            trainingMode = it.getString("trainingMode")
        }

        Log.d("checkedArabicWords", checkedArabicWords.forEach{ println(it) }.toString())
        Log.d("checkedRussianWords", checkedRussianWords.forEach{ println(it) }.toString())

        binding.showAnswerSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Если переключатель включен, показываем ответ
            if (isChecked) {
                binding.answerWord.visibility = View.VISIBLE
                val answer = when (trainingMode) {
                    "Ar-Ru" -> currentWord?.second
                    "Ru-Ar" -> currentWord?.first
                    "Merge" -> if (binding.textWord.text == currentWord?.first) currentWord?.second else currentWord?.first
                    else -> currentWord?.second
                }
                binding.answerWord.text = answer
            } else {
                // Если переключатель выключен, скрываем ответ
                binding.answerWord.visibility = View.INVISIBLE
            }
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
        } ?: "" // Если текущего слова нет, показываем пустую строку

        binding.textWord.text = displayedWord

        // Скрываем ответ, если переключатель выключен
        binding.answerWord.visibility = View.INVISIBLE
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
        // Анимация поворота карточки
        binding.cardWord.animate().rotationY(90f).setDuration(150).withEndAction {
            binding.textWord.text = answer
            binding.cardWord.rotationY = -90f
            binding.cardWord.animate().rotationY(0f).setDuration(150).start()

            Handler(Looper.getMainLooper()).postDelayed({
                // Показать следующее слово
                displayNextWord()
            }, 100)
        }.start()
    }
}
