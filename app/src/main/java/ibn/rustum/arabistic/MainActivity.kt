package ibn.rustum.arabistic

import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.window.SplashScreen
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import ibn.rustum.arabistic.databinding.ActivityMainBinding
import ibn.rustum.arabistic.util.SharedPreferencesUtils

class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController
    lateinit var binding: ActivityMainBinding
    lateinit var appBarConfiguration: AppBarConfiguration

    // NavController для каждой вкладки
    lateinit var booksNavController: NavController
    lateinit var mainSwipeNavController: NavController
    lateinit var appAboutNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val nightIcon: Int =
            SharedPreferencesUtils.getInteger(this, "nightIcon", R.drawable.vectornightpress)

        App.instance?.setNightMode()

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)


        // Инициализируем NavController
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main)

        // Инициализация AppBarConfiguration для toolbar (если он есть)
        appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()

        setupWithNavController(binding.navView, navController)
        setupWithNavController(Toolbar(baseContext), navController, appBarConfiguration)

        // Настройка NavController с BottomNavigationView
        val bottomNav = binding.navView
        bottomNav.setupWithNavController(navController)


        // Настройка NavController для основной навигации
        /*booksNavController = findNavController(R.id.books_nav_host)
        mainSwipeNavController = findNavController(R.id.mainSwipe_nav_host)
        appAboutNavController = findNavController(R.id.app_about_nav_host)

        // Настройка навигации для BottomNavigation
        binding.navView.setupWithNavController(booksNavController)

        binding.navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_books -> {
                    booksNavController.navigate(R.id.booksFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_main -> {
                    mainSwipeNavController.navigate(R.id.main_swipe_nav_graph)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.app_about -> {
                    appAboutNavController.navigate(R.id.appAboutFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }
        }*/

        // Если фрагмент не сохранен, делаем начальный переход
        if (savedInstanceState == null) {
            navController.navigate(R.id.booksFragment)
        }
    }

    // Обработчик нажатий на кнопку "назад"
    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        /*//if (id == R.id.settings) {
        val navController = findNavController(this, R.id.nav_host_fragment_content_main)
        val currentDestination = navController.currentDestination
        if (currentDestination != null && currentDestination.id == R.id.settingsFragment) {
            return true
        }
        navController.navigate<Any>(R.id.settingsFragment2)
        //return true
        //}*/

        return super.onOptionsItemSelected(item)
    }
}
