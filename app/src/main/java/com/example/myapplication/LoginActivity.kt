package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.model.GoogleUserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    //Binding View
    private lateinit var binding: ActivityLoginBinding

    //Firebase Auth and Database
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    //Google sign-in
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInOptions: GoogleSignInOptions

    //Email, Password strings
    private var email: String?= null
    private var password: String?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initializing Firebase auth
        auth = FirebaseAuth.getInstance()
        //Initializing Firebase Database
        database = FirebaseDatabase.getInstance().reference


        binding.loginbutton.setOnClickListener {
            email = binding.loginEmail.text.toString().trim()
            password = binding.loginPassword.text.toString().trim()
            if(email.isNullOrBlank() || password.isNullOrBlank()){
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
            } else {
                signIn(email!!, password!!)
            }
        }

        //Google Sign In
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.default_web_client_id)).
                requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.loginGoogleButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }

        binding.signUpTextButton.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private val launcher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
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

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                updateUi()
                finish()
            } else {
                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                Log.e("Account", "signIn: Failed", task.exception)
            }
        }

    }

    private fun updateUi() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    //If user is logged in, directly redirect to main page
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            updateUi()
            finish()
        }
    }
}