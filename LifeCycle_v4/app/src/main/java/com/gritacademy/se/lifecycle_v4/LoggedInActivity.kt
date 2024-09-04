package com.gritacademy.se.lifecycle_v4

import android.os.Bundle
import android.provider.MediaStore.Audio.Radio
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.Date
import kotlin.properties.Delegates

class LoggedInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var phoneNumber: String
    private lateinit var gender: String
    private lateinit var userId: String
    private lateinit var loggedInScared: Switch
    private lateinit var loggedInGenderGroup: RadioGroup
    private lateinit var maleGender: View
    private lateinit var femaleGender: View
    private lateinit var otherGender: View
    private var scared by Delegates.notNull<Boolean>()
    private var driversLicense by Delegates.notNull<Boolean>()
    private lateinit var loggedInEmail: TextView
    private lateinit var loggedInPhoneNumber: TextView
    private lateinit var loggedInPassword: TextView
    private lateinit var loggedInDriversLicense: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_logged_in2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        db = Firebase.firestore

        loggedInGenderGroup = findViewById(R.id.loggedInGenderGroup)
        loggedInEmail = findViewById(R.id.loggedInEmail)
        loggedInPhoneNumber = findViewById(R.id.loggedInPhoneNumber)
        loggedInPassword = findViewById(R.id.loggedInPassword)
        loggedInDriversLicense = findViewById(R.id.loggedInDriversLicense)
        loggedInScared = findViewById(R.id.loggedInScared)
        maleGender = findViewById(R.id.male);
        femaleGender = findViewById(R.id.female);
        otherGender = findViewById(R.id.other);

        val user = Firebase.auth.currentUser
        if (user != null) {
            let {
                email = user.email.toString()
                loggedInEmail.text = email
                userId = user.uid;
            }
            Log.i("Sam", "${userId} email:${email}")

        }

        val docRef = db.collection("users").document(userId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("Sam", "DocumentSnapshot data: ${document.data}")
                    password = document.get("password").toString();
                    loggedInPassword.text = password
                    gender = document.get("gender").toString().trim();
                    Log.i("Sam", gender)
                    phoneNumber = document.get("phone").toString();
                    loggedInPhoneNumber.text = phoneNumber;
                    scared = document.get("scared") as Boolean
                    loggedInScared.isChecked = scared
                    driversLicense = document.get("driving license") as Boolean
                    loggedInDriversLicense.isChecked = driversLicense

                    when (gender) {
                        "male" -> {
                           // val idx: Int = loggedInGenderGroup.indexOfChild(maleGender)
                            loggedInGenderGroup.check(maleGender.id)
                        }


                        "female" -> {
                            //val idx: Int = loggedInGenderGroup.indexOfChild(femaleGender)
                            loggedInGenderGroup.check(femaleGender.id)
                        }


                        "other" -> {
                            //val idx: Int = loggedInGenderGroup.indexOfChild(otherGender)
                            loggedInGenderGroup.check(otherGender.id)
                            Log.i("Sam", "found it")
                        }
                        else ->{
                            Log.i("Sam", "Could not find it")
                        }

                    }

                } else {
                    Log.d("Sam", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Sam", "get failed with ", exception)
            }



    }
}