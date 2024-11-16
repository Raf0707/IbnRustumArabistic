package ibn.rustum.arabistic


import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import ibn.rustum.arabistic.util.SharedPreferencesUtils

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        setNightMode()
    }

    fun setNightMode() {
        // Чтение текущего значения ночного режима из SharedPreferences
        val nightMode = SharedPreferencesUtils.getInteger(this, "nightMode", 1)

        // Массив возможных режимов ночной темы
        val modes = intArrayOf(
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY,
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,  // Использовать системные настройки
            AppCompatDelegate.MODE_NIGHT_NO,            // Режим светлой темы
            AppCompatDelegate.MODE_NIGHT_YES           // Режим темной темы

        )

        // Применяем выбранный режим
        AppCompatDelegate.setDefaultNightMode(modes[nightMode])
    }

    companion object {
        // Экземпляр приложения (синглтон)
        internal var instance: App? = null

        // Метод для получения экземпляра приложения
        fun getInstance(): App {
            return instance ?: throw IllegalStateException("Application not initialized")
        }
    }
}
