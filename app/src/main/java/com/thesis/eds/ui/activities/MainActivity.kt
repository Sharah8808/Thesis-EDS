package com.thesis.eds.ui.activities

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavHostController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.thesis.eds.R
import com.thesis.eds.databinding.ActivityMainBinding
import com.thesis.eds.utils.interfaces.ActionBarTitleSetter
import com.thesis.eds.utils.interfaces.MenuItemHighlighter
import com.thesis.eds.utils.DialogUtils

class MainActivity : AppCompatActivity(), ActionBarTitleSetter, MenuItemHighlighter {

    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        setSupportActionBar(mainBinding.appBarMain.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val drawerLayout: DrawerLayout = mainBinding.drawerLayout
        val navView: NavigationView = mainBinding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main) as NavHostController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_diagnostic,
                R.id.nav_history,
                R.id.nav_disease_list,
                R.id.nav_setting
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
//        Toast.makeText(this, appBarConfiguration.toString() + "eeehhh", Toast.LENGTH_SHORT).show()

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

//    override fun onBackPressed() {
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        if (navController.currentDestination?.id == R.id.nav_home) {
//            // If the current destination is the home fragment, exit the app
//            super.onBackPressed()
//        } else {
//            // Otherwise, navigate up in the navigation hierarchy
//            navController.navigateUp()
//        }
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        Log.d(ContentValues.TAG, " di MAINACITIVTY AAA current navcont = ${navController.currentDestination?.id}          !!!  ---------------------------------------------- r.id diagresult frag? ${R.id.diagnosticResultFragment}")

        when (item.itemId) {
            android.R.id.home -> {
                if (navController.currentDestination?.id == R.id.diagnosticResultFragment) {
                    Log.d(ContentValues.TAG, " di MAINACITIVTY AAA di dalem fungsi wen HOME HOME current navcont = ${navController.currentDestination?.id}          !!!  ---------------------------------------------- r.id diagresult frag? ${R.id.diagnosticResultFragment}")
                    DialogUtils.showExitAlertDialog(this) {
                        // Go back to the previous fragment and lose the current picture data
                        navController.navigateUp()
                    }
//                    navController.navigateUp()
                    return true
                } else if (navController.currentDestination?.id == R.id.editProfileFragment){
                    DialogUtils.showExitAlertDialog(this){
                        navController.navigateUp()
                    }
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun setTitle(title: String?) {
        supportActionBar?.title = title;
    }

    override fun setMenuHighlight(idIndex: Int?) {
        val navView: NavigationView = mainBinding.navView
        idIndex?.let { navView.menu.getItem(it).setChecked(true) }
    }

}