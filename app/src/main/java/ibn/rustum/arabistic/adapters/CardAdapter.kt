package ibn.rustum.arabistic.adapters

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import ibn.rustum.arabistic.databinding.WordLinearItemBinding
import java.io.File

class CardAdapter(
    private val items: List<Pair<String, String>>,
    private val context: Context // Передаем контекст для доступа к Google Translate TTS
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

            // Устанавливаем обработчик для кнопки listen
            listen.setOnClickListener {
                // Вызываем функцию для озвучивания арабского текста
                playTextFromGoogleTranslate(context, arabicWord, "ar")
            }
        }
    }

    override fun getItemCount() = items.size

    // Функция для воспроизведения аудио с Google Translate
    fun playTextFromGoogleTranslate(context: Context, text: String, language: String) {
        val url = "https://translate.google.com/translate_tts?ie=UTF-8&q=${Uri.encode(text)}&tl=$language&client=tw-ob"
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(url)
        mediaPlayer.setOnPreparedListener {
            it.start() // Воспроизводим аудио
        }
        mediaPlayer.setOnErrorListener { _, _, _ ->
            Toast.makeText(context, "Ошибка воспроизведения", Toast.LENGTH_SHORT).show()
            true
        }
        mediaPlayer.prepareAsync() // Асинхронная подготовка
    }

    // Метод для загрузки и воспроизведения аудио
    @RequiresApi(Build.VERSION_CODES.O)
    fun downloadAndPlayAudio(context: Context, text: String, language: String) {
        val url = "https://translate.google.com/translate_tts?ie=UTF-8&q=${Uri.encode(text)}&tl=$language&client=tw-ob"
        val fileName = "tts_${text.hashCode()}.mp3"  // Создаем уникальное имя файла на основе хеша текста
        val filePath = File(context.cacheDir, fileName)  // Путь к файлу в кеше

        // Если файл уже существует в кеше, воспроизводим его
        if (filePath.exists()) {
            playAudio(context, filePath)
        } else {
            // Если файла нет, начинаем загрузку
            val request = DownloadManager.Request(Uri.parse(url))
            request.setDestinationUri(Uri.fromFile(filePath))  // Указываем место для сохранения файла

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadId = downloadManager.enqueue(request)

            // Регистрируем BroadcastReceiver для получения уведомления о завершении загрузки
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) == downloadId) {
                        // После завершения загрузки воспроизводим файл
                        playAudio(context, filePath)
                        context.unregisterReceiver(this)  // Отписываемся от BroadcastReceiver
                    }
                }
            }

            // Регистрируем BroadcastReceiver, который будет слушать завершение загрузки
            context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                Context.RECEIVER_EXPORTED)
        }
    }

    // Метод для воспроизведения локального аудио файла
    fun playAudio(context: Context, file: File) {
        try {
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(file.path)  // Указываем путь к локальному файлу
            mediaPlayer.prepare()  // Подготовка
            mediaPlayer.start()  // Воспроизведение
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка воспроизведения аудио: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

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
