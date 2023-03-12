package com.thesis.eds

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavHostController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.thesis.eds.data.History
import com.thesis.eds.databinding.ActivityMainBinding
import com.thesis.eds.interfaces.ActionBarTitleSetter
import com.thesis.eds.interfaces.MenuItemHighlighter

class MainActivity : AppCompatActivity(), ActionBarTitleSetter, MenuItemHighlighter {

    //from private
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var mainBinding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        setSupportActionBar(mainBinding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = mainBinding.drawerLayout
        val navView: NavigationView = mainBinding.navView
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

        navView.setNavigationItemSelectedListener {menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val navController = findNavController(R.id.nav_host_fragment_content_main)
                    navController.navigate(R.id.nav_home)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_diagnostic -> {
                    navController.navigate(R.id.nav_diagnostic)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_history -> {
                    navController.navigate(R.id.nav_history)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_disease_list -> {
                    navController.navigate(R.id.nav_disease_list)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_setting -> {
                    navController.navigate(R.id.nav_setting)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                // Handle other menu items
                else -> false
            }

        }

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
        val navView: NavigationView = mainBinding.navView
        Toast.makeText(this, "is highlight called on main?!?!?!", Toast.LENGTH_SHORT).show()
        idIndex?.let { navView.menu.getItem(it).setChecked(true) }
    }


}