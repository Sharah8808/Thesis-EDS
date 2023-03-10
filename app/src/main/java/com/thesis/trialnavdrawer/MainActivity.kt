package com.thesis.trialnavdrawer

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavHostController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.thesis.trialnavdrawer.databinding.ActivityMainBinding
import com.thesis.trialnavdrawer.interfaces.ActionBarTitleSetter
import com.thesis.trialnavdrawer.interfaces.MenuItemHighlighter

class MainActivity : AppCompatActivity(), ActionBarTitleSetter, MenuItemHighlighter {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main) as NavHostController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_diagnostic, R.id.nav_history, R.id.nav_disease_list, R.id.nav_setting
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        Toast.makeText(this, appBarConfiguration.toString() + "eeehhh", Toast.LENGTH_SHORT).show()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        Toast.makeText(this, "ORAOROAORA", Toast.LENGTH_SHORT).show()
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun setTitle(title: String?) {
        supportActionBar?.title = title;
    }

    override fun setMenuHighlight(idIndex: Int?) {
        val navView: NavigationView = binding.navView
        Toast.makeText(this, "is highlight called on main?!?!?!", Toast.LENGTH_SHORT).show()
        idIndex?.let { navView.menu.getItem(it).setChecked(true) }
    }
}