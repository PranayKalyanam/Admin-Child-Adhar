package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.databinding.ActivityFingerPrintBinding
import com.example.myapplication.model.LostChildDetailsModel
import com.google.firebase.database.FirebaseDatabase

class FingerPrintActivity : AppCompatActivity() {
    //Binding View
    private lateinit var binding: ActivityFingerPrintBinding

    //Firebase database
    private val database = FirebaseDatabase.getInstance().reference

    //family id and child id to save details in database
    private var childId: String?= null
    private var familyId: String?= null

    //variables to store lost children data
    private var childFirstName:String?= null
    private var childLastName:String?= null
    private var childFatherName:String?= null
    private var childFatherPhone:Int?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFingerPrintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //retrieve data from previous activity
        childId = intent.getStringExtra("child_id")
        familyId = intent.getStringExtra("family_id")
        childFirstName = intent.getStringExtra("child_first_name")
        childLastName = intent.getStringExtra("child_last_name")
        childFatherName = intent.getStringExtra("father_name")
        childFatherPhone = intent.getIntExtra("father_phone", -1)

        if(childId != null && familyId != null){
            //  fingerprint scanning logic
            binding.fingerPrintRegisterButton.setOnClickListener {
                // Simulate fingerprint scan
                simulateFingerprintScan(childId!!)
            }
        } else {
            Toast.makeText(this, "Missing child or family data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun simulateFingerprintScan(childId: String) {
        // Simulate fingerprint (use a static key for now)
        val simulatedFingerprintKey = "sample-fingerprint-key-12345" // Static fingerprint key for simulation

        // Create a map to update the child node with the fingerprint
        val childUpdates = mapOf(
            "fingerprint" to simulatedFingerprintKey
        )

        // Update child node with simulated fingerprint
        database.child("Families").child(familyId!!).child("children")
            .child(childId).updateChildren(childUpdates)
            .addOnSuccessListener {
                val lostChildDetailsModel = LostChildDetailsModel(childFirstName, childLastName, childFatherName, childFatherPhone )
                // Save lost child information to the LostChildren node
                database.child("LostChildren").child(childId).setValue(lostChildDetailsModel)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Child details saved successfully", Toast.LENGTH_SHORT)
                            .show()
                        //after saving data return to main page and finish this page
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Close the activity after saving
                    }
                    .addOnFailureListener{
                            e->
                        Toast.makeText(this, "Failed to save fingerprint: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener{ e->
                Toast.makeText(this, "Failed to save fingerprint: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}