package ibn.rustum.arabistic

import android.os.Bundle
import android.view.Window
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import ibn.rustum.arabistic.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController
    lateinit var binding: ActivityMainBinding
    lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        navController = findNavController(this, R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()
        //setupWithNavController(binding.bottomAppBar, navController)
        //setupWithNavController(binding.toolbar, navController, appBarConfiguration)

        if (savedInstanceState == null) {
            // Переход к BookFragment
            navController.navigate(R.id.booksFragment)
        }
    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.settings) {
            val navController = findNavController(this, R.id.nav_host_fragment_content_main)
            val currentDestination = navController.currentDestination
            if (currentDestination != null && currentDestination.id == R.id.settingsFragment) {
                return true
            }
            navController.navigate<Any>(R.id.settingsFragment2)
            return true
        }

        return super.onOptionsItemSelected(item)
    }*/

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(this, R.id.nav_host_fragment_content_main)
        return navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}