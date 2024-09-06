package com.gritacademy.se.lifecycle_v4

import android.content.Intent
import android.icu.text.DateFormat
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import kotlin.properties.Delegates

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerPhoneNumber: TextView
    private lateinit var registerPassword: TextView
    private lateinit var registerEmail: TextView
    private lateinit var genderGroup: RadioGroup
    private lateinit var finishRegisterBtn: Button
    private lateinit var driversLicense: CheckBox
    private lateinit var auth: FirebaseAuth
    private lateinit var radioButton: RadioButton
    private var idx by Delegates.notNull<Int>()
    private lateinit var r: RadioButton
    private lateinit var selectedGender: String
    lateinit var db: FirebaseFirestore
    private lateinit var loggedInIntent: Intent
    private lateinit var registerIntent: Intent
    private lateinit var registerScared:Switch
    private lateinit var registerLogoutBtn:Button
    private lateinit var registerProfileBtn:Button
    private lateinit var registerLoginBtn:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

         loggedInIntent = Intent(this,LoggedInActivity::class.java)
         registerIntent = Intent(this,RegisterActivity::class.java)
        val logInIntent:Intent = Intent(this,MainActivity::class.java)

        db = Firebase.firestore
        registerEmail = findViewById(R.id.regEmail)
        registerPassword = findViewById(R.id.regPassword)
        registerPhoneNumber = findViewById(R.id.regPhoneNumber)
        genderGroup = findViewById(R.id.regGenderGroup)
        driversLicense = findViewById(R.id.regDriversLicense)
        registerScared = findViewById(R.id.registerScared)
        finishRegisterBtn = findViewById(R.id.finishRegistrationBtn)
        registerLogoutBtn = findViewById(R.id.registerLogoutBtn)
        registerLoginBtn = findViewById(R.id.registerLoginBtn)
        registerProfileBtn = findViewById(R.id.registerProfileBtn)
        auth = Firebase.auth

        finishRegisterBtn.setOnClickListener {
            register()
        }

        registerLogoutBtn.setOnClickListener{
            if (auth.currentUser!=null){
                auth.signOut()
                startActivity(logInIntent)
            }
            else{
                Toast.makeText(baseContext, "You need to login to logout", Toast.LENGTH_SHORT).show()
            }
        }
        registerLoginBtn.setOnClickListener{
            startActivity(logInIntent)
        }
        registerProfileBtn.setOnClickListener{
            if (auth.currentUser!=null){
                startActivity(loggedInIntent)
            }
            else{
                Toast.makeText(baseContext, "login or create an account", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun register() {
        val currentUser: FirebaseUser? = auth.currentUser
        val radioButtonID: Int = genderGroup.checkedRadioButtonId;
        try {
            radioButton = genderGroup.findViewById(radioButtonID)
            idx = genderGroup.indexOfChild(radioButton);
            r = genderGroup.getChildAt(idx) as RadioButton
            selectedGender = r.text.toString().trim()
        } catch (e: Exception) {
            selectedGender = "male".trim()
            Log.i("Sam", "Exception: ${selectedGender}")
        }



         val email: String = registerEmail.text.toString().trim();
         val password: String = registerPassword.text.toString().trim();
         val phoneNumber:String = registerPhoneNumber.text.toString().trim()
         val license:Boolean = driversLicense.isChecked
         val scared:Boolean = registerScared.isChecked

        if (currentUser != null) {
            auth.signOut()
            register()
        } else if (password.isNotEmpty() && email.isNotEmpty()&& phoneNumber.isNotEmpty() && selectedGender.isNotEmpty()
        ) {
            Log.i("Sam", "email:${email}")
            Log.i("Sam", "password:${password}")
            Log.i("Sam", "phone:${phoneNumber}")
            Log.i("Sam", "license:${license}")
            Log.i("Sam", "gender:${selectedGender}")

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Sam", "createUserWithEmail:success")

                        val userX = Firebase.auth.currentUser


                        val user = hashMapOf(
                            "password" to password,
                            "phone" to phoneNumber.toInt(),
                            "gender" to selectedGender,
                            "driving license" to license,
                            "scared" to scared
                        )

                        if (userX != null) {
                            var userID = userX.uid
                            Log.i("Sam", "register:${userID} ")
                            db.collection("users").document(userID).set(user).addOnSuccessListener {
                                Log.d(
                                    "Sam",
                                    "DocumentSnapshot successfully written! ${userID}"
                                )
                                startActivity(loggedInIntent);

                            }
                                .addOnFailureListener { e ->
                                    Log.w(
                                        "Sam",
                                        "Error writing document",
                                        e
                                    )
                                }
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Sam", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }


        } else {
            Toast.makeText(this, "make sure everything is filled in", Toast.LENGTH_SHORT).show()
        }
    }
}