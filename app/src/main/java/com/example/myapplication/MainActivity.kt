package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    //Binding View
    private lateinit var binding: ActivityMainBinding

    //Firebase auth
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = auth.currentUser?.uid

        if(currentUser == null){
            Toast.makeText(this, "No user id available", Toast.LENGTH_SHORT).show()
        }
        binding.familyDetailsButton.setOnClickListener {
            val intent = Intent(this, FamilyDetailsActivity::class.java)
            startActivity(intent)
        }

        binding.childDetailsButton.setOnClickListener {
            val intent = Intent(this, AddChildrenActivity::class.java)
            startActivity(intent)
        }

        binding.logOutButton.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}