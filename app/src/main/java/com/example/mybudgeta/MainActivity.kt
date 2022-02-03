package com.example.mybudgeta

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    val auth = Helper.auth
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var navController: NavController
    private lateinit var navHostFragment: Fragment

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
        navView = findViewById(R.id.navigation_view)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)!!
        navHostFrag = navHostFragment

        val appMenuIcon: ImageView = findViewById(R.id.app_menu_icon)
        appMenuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(navView, navController)

        val appTitleTv:TextView = findViewById(R.id.app_title)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            appTitleTv.text = destination.label
        }

        val currUser = auth.currentUser
        if(currUser == null) {
            Helper.showToast(this, "Login required!")
            val loginInt = Intent(this, LoginActivity::class.java)
            startActivity(loginInt)
        }

        /*supportActionBar?.setDisplayHomeAsUpEnabled(true)*/

        /*supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, MainFragment())
            .commit()*/

        /*navView.setNavigationItemSelectedListener {
            it.isChecked = true // highlight selected menu item
            var fragment: Fragment? = null

            when(it.itemId) {
                R.id.menu_item_budget -> fragment = BudgetFragment()
                R.id.menu_item_all_expenses -> showToast("ALL EXPENSES")
                R.id.menu_item_lunch -> fragment = LunchFragment()
                R.id.menu_item_transport -> fragment = TransportFragment()
                R.id.menu_item_snacks -> fragment = SnacksFragment()
                R.id.menu_item_extra1 -> showToast("EXTRA EXPENSES 1")
                R.id.menu_item_extra2 -> showToast("EXTRA EXPENSES 2")
                R.id.menu_item_extra3 -> showToast("EXTRA EXPENSES 3")
            }
            if(fragment != null) {
                replaceFragment(fragment, it.title.toString())
            }
            true
        }*/
    }

    // launch nav drawer when hamburger menu is clicked
    /*override fun onSupportNavigateUp(): Boolean {
        drawerLayout.openDrawer(navView)
        return true
    }*/

    // close nav drawer when back button is clicked
    /*override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }*/

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }*/

    /*private fun replaceFragment(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()

        // set activity title & close drawer
        drawerLayout.closeDrawers()
        setTitle(title)
    }*/
}