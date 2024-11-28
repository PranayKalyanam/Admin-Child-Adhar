package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivitySignUpBinding
import com.example.myapplication.model.GoogleUserModel
import com.example.myapplication.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    //Binding View
    private lateinit var binding: ActivitySignUpBinding

    //Firebase Auth and Database
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    //Google Sign In
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInOptions: GoogleSignInOptions

    //username, email, password string
    private var userName: String?= null
    private var email: String?= null
    private var password: String?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initializing firebase auth and database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding.signUpButton.setOnClickListener {
            //get text from view
            userName = binding.signUpName.text.toString().trim()
            email = binding.signUpEmail.text.toString().trim()
            password = binding.signUpPassword.text.toString().trim()

            if(userName.isNullOrBlank() || email.isNullOrBlank() || password.isNullOrBlank()){
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
            } else{
                createAccount(email!!, password!!)
            }
        }

        //Google Sign In
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
            requestIdToken(getString(R.string.default_web_client_id)).
            requestEmail().
            build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        binding.signUpGoogleButton.setOnClickListener {
            val signIntent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }

        binding.loginTextButton.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private val launcher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            if(task.isSuccessful){
                val account = task.result
                val userName = account.displayName
                val userEmail = account.email
                val userIdToken = account.idToken
                val credential = GoogleAuthProvider.getCredential(userIdToken, null)

                auth.signInWithCredential(credential).addOnCompleteListener {task1->
                    if(task1.isSuccessful){
                        saveGoogleUserData(userName, userEmail)
                        updateUi()
                        finish()
                    } else{
                        Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveGoogleUserData(userName: String?, userEmail: String?) {
        val currentUser = auth.currentUser
        val googleUserModel = GoogleUserModel(userName, userEmail,null, null, null)
        currentUser?.let{
            val userId = it.uid
            database.child("user").child(userId).setValue(googleUserModel)
        }

    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                val user = auth.currentUser
                Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                saveUserData(user)
                updateUi()
                finish()
            } else{
                Toast.makeText(this, "Account is not Created", Toast.LENGTH_SHORT).show()

            }
        }

    }

    private fun saveUserData(user: FirebaseUser?) {
        userName = binding.signUpName.text.toString()
        email = binding.signUpEmail.text.toString()
        password = binding.signUpPassword.text.toString()

        val userModel = UserModel(userName, email, password, null )
        if(user != null){
            //get a unique  id before saving
            val userId = user.uid
            //save user data in firebase database
            database.child("users").child(userId).setValue(userModel)
        } else{
            Log.e("saving_user_data", "Current_user: null")
        }


    }

    private fun updateUi() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}