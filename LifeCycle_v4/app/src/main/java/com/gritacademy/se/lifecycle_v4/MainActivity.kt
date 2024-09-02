package com.gritacademy.se.lifecycle_v4

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val loginBtn = findViewById<Button>(R.id.loginBtn);
        val emailUserInput = findViewById<TextView>(R.id.emailInput);
        val passwordInput = findViewById<TextView>(R.id.passwordInput);

        val db = Firebase.firestore
        val loggedInIntent = Intent(this,LoggedInActivity::class.java)

        loginBtn.setOnClickListener {
            db.collection("users")
                .whereEqualTo("email", emailUserInput.text.toString()).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d("Sam", "${document.id} => ${document.data}")
                        var password = document.get("password");
                        if (password != null) {
                            if (password.equals(passwordInput.text.toString())){
                                Log.i("Sam", "You Logged In ")
                                startActivity(loggedInIntent)

                            }

                        }
                    }
                }.addOnFailureListener{ e ->
                    Log.w("Sam", "exception: ", e )
                }
        }



        /*db.collection("users")
            .whereEqualTo("firstname", "Sam")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("Sam", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Sam", "Error getting documents: ", exception)
            }*/

        // Create a new user with a first and last name
        /* val user = hashMapOf(
             "first" to "Ada",
             "last" to "Lovelace",
             "born" to 1815,
         )*/

        // Add a new document with a generated ID
       /* db.collection("users").add(user).addOnSuccessListener { documentReference ->
            Log.d("Sam", "DocumentSnapshot added with ID: ${documentReference.id}")
        }.addOnFailureListener { e ->
            Log.w("Sam", "Error adding document", e)
        }*/

    }


}