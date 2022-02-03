package com.example.mybudgeta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    val TAG = "Login Activity"
    val auth = Helper.auth
    val db = Helper.db

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // register
        login_signup_tv.setOnClickListener {
            val registerInt = Intent(this, RegisterActivity::class.java)
            startActivity(registerInt)
        }

        // login
        login_login_btn.setOnClickListener { view ->
            Helper.showToast(this, "PROCESSING...")
            val email = Helper.trimText(login_email_tf)
            val password = Helper.trimText(login_password_tf)

            if(email.isEmpty() || password.isEmpty()) {
                Helper.showToast(this, "Fill all fields!")
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if(!task.isSuccessful) {
                    Helper.showToast(this, "An error occurred! Couldn't log you in.")
                    return@addOnCompleteListener
                }
                val currUserId = auth.currentUser!!.uid
                val dbUsers = db.child(Helper.DB_REF_USERS)
                dbUsers.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.hasChild(currUserId)) {
                            redirectToMain()
                        } else {
                            Helper.showToast(view.context, "User NOT found!")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "signInWithEmailAndPassword:onCancelled", error.toException())
                    }
                })
            }
        }
    }

    private fun redirectToMain() {
        val homeIntent = Intent(this, MainActivity::class.java)
        homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(homeIntent)
    }
}