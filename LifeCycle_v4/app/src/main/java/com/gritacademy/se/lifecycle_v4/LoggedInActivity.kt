package com.gritacademy.se.lifecycle_v4

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
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
    private lateinit var finishChangesBtn: Button
    private lateinit var selectedGender: String
    private lateinit var loggedInLogOutBtn:Button
    private lateinit var loggedInRegisterBtn:Button


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
        finishChangesBtn = findViewById(R.id.changeProfileBtn)
        loggedInLogOutBtn = findViewById(R.id.loggedInLogOutBtn)
        loggedInRegisterBtn = findViewById(R.id.loggedInRegisterBtn)

        val user = Firebase.auth.currentUser
        val registerIntent = Intent(this,RegisterActivity::class.java)
        val logoutIntent = Intent(this,MainActivity::class.java)

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

                        else -> {
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

        finishChangesBtn.setOnClickListener {
            finishChanges()
        }

        loggedInLogOutBtn.setOnClickListener{
            auth.signOut()
            startActivity(logoutIntent)
        }
        loggedInRegisterBtn.setOnClickListener{
            startActivity(registerIntent)
        }

    }

    fun finishChanges() {
        val user = Firebase.auth.currentUser
        val newMail: String = loggedInEmail.text.toString().trim()
        val newPassword: String = loggedInPassword.text.toString().trim()
        val newPhone: String = loggedInPhoneNumber.text.toString().trim()
        val radioButton: View
        val idx: Int
        val r: RadioButton
        val newScaredCheck: Boolean = loggedInScared.isChecked
        val newDriversLicense = loggedInDriversLicense.isChecked
        val userID:String? = user?.uid;


        val radioButtonID: Int = loggedInGenderGroup.checkedRadioButtonId;
        try {
            radioButton = loggedInGenderGroup.findViewById(radioButtonID)
            idx = loggedInGenderGroup.indexOfChild(radioButton);
            r = loggedInGenderGroup.getChildAt(idx) as RadioButton
            selectedGender = r.text.toString().trim()
        } catch (e: Exception) {
            selectedGender = "male".trim()
            Log.i("Sam", "Exception: ${selectedGender}")
        }

        if (newMail.isNotEmpty() && newPassword.isNotEmpty() && newPhone.isNotEmpty() && selectedGender.isNotEmpty()) {
            user!!.updateEmail(newMail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Sam", "User email address updated.")
                    }
                }

            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Sam", "User password updated.")
                    }
                }

            val updatedUser = hashMapOf(
                "driving license" to newDriversLicense,
                "gender" to selectedGender,
                "password" to newPassword,
                "phone" to newPhone.toInt(),
                "scared" to newScaredCheck
            )

            db.collection("users").document(userID.toString())
                .set(updatedUser)
                .addOnSuccessListener { Log.d("Sam", "DocumentSnapshot successfully written!")
                    Toast.makeText(baseContext, "Profile Changed", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e -> Log.w("Sam", "Error writing document", e) }


        }
        else{
            Toast.makeText(baseContext, "Something went wrong", Toast.LENGTH_SHORT).show()
        }

    }
}