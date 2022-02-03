package com.example.mybudgeta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    val auth = Helper.auth
    val db =  Helper.db

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // login
        register_login_tv.setOnClickListener {
            val loginInt = Intent(this, LoginActivity::class.java)
            startActivity(loginInt)
        }

        // register
        register_register_btn.setOnClickListener {
            Helper.showToast(this, "PROCESSING...")
            val username = Helper.trimText(register_username_tf)
            val email = Helper.trimText(register_email_tf)
            val password = Helper.trimText(register_password_tf)

            if(username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Helper.showToast(this, "Fill all fields!")
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if(!it.isSuccessful) {
                    return@addOnCompleteListener
                }

                val currUserId = auth.currentUser!!.uid
                val currUserData = db.child(Helper.DB_REF_USERS).child(currUserId)
                currUserData.child("username").setValue(username)
                currUserData.child("profile").setValue("default")
                Helper.showToast(this, "Registration Successful!")

                // login
                val homeIntent = Intent(this, LoginActivity::class.java)
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(homeIntent)
            }
        }
    }
}