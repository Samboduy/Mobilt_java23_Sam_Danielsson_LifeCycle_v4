package com.gritacademy.se.lifecycle_v4

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var  db:FirebaseFirestore
    private lateinit var loginBtn:Button
    private lateinit var emailUserInput:TextView
    private lateinit var passwordInput:TextView
    private lateinit var registerBtn:Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        db = Firebase.firestore
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn)
        emailUserInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)


        val loggedInIntent = Intent(this,LoggedInActivity::class.java)
        val registerIntent = Intent(this,RegisterActivity::class.java)

        // See: https://developer.android.com/training/basics/intents/result

        // See: https://developer.android.com/training/basics/intents/result




        loginBtn.setOnClickListener {
            signIn(emailUserInput.text.toString(),passwordInput.text.toString())
            loggedInIntent.apply { }
            startActivity(loggedInIntent)
        }
        registerBtn.setOnClickListener{
            startActivity(registerIntent)
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

    private fun signIn(email:String,password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Sam", "signInWithEmail:success")
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Sam", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }


}