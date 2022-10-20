package com.example.mybudgeta

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView

import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import com.google.firebase.database.Query
import java.util.*


class MainActivity : AppCompatActivity() {
    private val auth = Helper.auth
    private val db = Helper.db
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView
    private lateinit var navHostFragment: Fragment
    private lateinit var appBarConfiguration: AppBarConfiguration

    companion object {
        var navHostFrag: Fragment? = null

        fun getNavHostFragment() : Fragment? {
            return navHostFrag
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.main_toolbar)
        navView = findViewById(R.id.navigation_view)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)!!
        navHostFrag = navHostFragment

        val navItems = setOf(
            R.id.menu_item_budget,
            R.id.menu_item_all_expenses,
            R.id.menu_item_lunch,
            R.id.menu_item_transport,
            R.id.menu_item_snacks,
            R.id.menu_item_other,
            R.id.menu_item_summary,
        )
        appBarConfiguration = AppBarConfiguration(navItems, drawerLayout)

        setSupportActionBar(toolbar)
        // change menu icon: NOT working properly
        toolbar.post {
            val menuIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_menu_alt, null)
            toolbar.navigationIcon = menuIcon
        }

        val navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val currUser = auth.currentUser

        if(currUser != null) {
            val navDrawerHeader = navView.getHeaderView(0)
            val usernameTf: TextView = navDrawerHeader.findViewById(R.id.nav_drawer_header_username)
            val greetingTf: TextView = navDrawerHeader.findViewById(R.id.nav_drawer_header_greeting)

            val query: Query = db.child(Helper.DB_REF_USERS).child(currUser.uid)
            query.get().addOnSuccessListener { snapshot ->
                if(!snapshot.exists())
                    return@addOnSuccessListener

                val username = snapshot.child(Helper.USER_ATTR_USERNAME).value.toString()
                usernameTf.text = username
                greetingTf.text = greeting()

            }.addOnFailureListener {
                Helper.showToast(this, "getUsername:addOnFailureListener")
            }

        } else {
            Helper.showToast(this, "Login required!")
            val loginInt = Intent(this, LoginActivity::class.java)
            startActivity(loginInt)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_item_logout -> logout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        auth.signOut()
        val loginInt = Intent(this, LoginActivity::class.java)
        startActivity(loginInt)
    }

    fun greeting(): String? {
        val greeting: String?
        val calendar: Calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        if(hourOfDay in 0..11)
            greeting = "Good morning!"
        else if(hourOfDay in 12..16)
            greeting = "Good afternoon!"
        else if(hourOfDay in 17..20)
            greeting = "Good evening!"
        else if(hourOfDay in 21..23)
            greeting = "Good night!"
        else
            greeting = null
        return greeting
    }

}