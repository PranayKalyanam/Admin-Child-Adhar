package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.databinding.ActivityChildDetailsBinding
import com.example.myapplication.model.ChildDetailsModel
import com.example.myapplication.model.LostChildDetailsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ChildDetailsActivity : AppCompatActivity() {
    //Binding View
    private lateinit var binding: ActivityChildDetailsBinding

    //Firebase auth and database
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    //the familyId to save child details
    private var familyId: String?= null

    //Strings to store from edit text
    private var childFirstName:String?=null
    private var childSurName:String?=null
    private var childFatherName:String?=null
    private var childMotherName:String?=null
    private var childDateOfBirth:String?=null
    private var childTimeOfBirth:String?=null
    private var childPlaceOfBirth:String?=null
    private var childGender:String?=null
    private var childDisability:String?=null
    private var childAddress:String?=null

    // Some Extra information for lost children
    private var childFatherPhone: Int?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChildDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get familyId from user's data
        val userId = auth.currentUser?.uid
        if(userId != null){
            // Fetch the familyId from the user node in Firebase
            database.child("users").child(userId).child("familyId").get()
                .addOnSuccessListener {
                    familyId = it.getValue(String::class.java)
                    if(familyId == null){
                        Toast.makeText(this, "No family associated with this user", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.childDetailsSaveButton.setOnClickListener {
            saveChildDetails()
        }
    }

    private fun saveChildDetails() {
        // Collect child details from the input fields
         childFirstName = binding.childFirstNameEditText.text.toString().trim()
         childSurName = binding.childSurnameEditText.text.toString().trim()
         childFatherName = binding.childFatherNameEditText.text.toString().trim()
         childMotherName = binding.childMotherNameEditText.text.toString().trim()
         childFatherPhone = binding.childFatherPhoneEditText.text.toString().toIntOrNull()
         childDateOfBirth = binding.childDateOfBirthEditText.text.toString().trim()
         childTimeOfBirth = binding.childTimeOfBirthEditText.text.toString().trim()
         childPlaceOfBirth = binding.childPlaceOfBirthEditText.text.toString().trim()
         childGender = binding.childGenderEditText.text.toString().trim()
         childDisability = binding.childDisabilityEditText.text.toString().trim()
         childAddress = binding.childAddressEditText.text.toString().trim()

        if(
            childFirstName.isNullOrBlank() || childSurName.isNullOrBlank() ||
            childFatherName.isNullOrBlank() || childMotherName.isNullOrBlank() ||
            childFatherPhone == null || childDateOfBirth.isNullOrBlank() ||
            childTimeOfBirth.isNullOrBlank() || childPlaceOfBirth.isNullOrBlank() ||
            childGender.isNullOrBlank() || childDisability.isNullOrBlank() ||
            childAddress.isNullOrBlank()
        ) {
            Toast.makeText(this, "Please fill in all child details", Toast.LENGTH_SHORT).show()
            return
        } else {
            // Check if familyId exists
            if (familyId == null) {
                Toast.makeText(this, "No family associated with this user", Toast.LENGTH_SHORT).show()
                return
            } else {
                // Create ChildDetailsModel
                val childDetailsModel = ChildDetailsModel(
                    childFirstName, childSurName, childFatherName, childMotherName, childFatherPhone,
                    childDateOfBirth, childTimeOfBirth, childPlaceOfBirth, childGender, childDisability,
                    childAddress, null //This will be updated after simulating fingerprint submission
                )

                // Save child details to the family node under "children"
                val childId = database.child("Families").child(familyId!!).child("children").push().key
                if(childId != null){
                    database.child("Families").child(familyId!!).child("children").child(childId).setValue(childDetailsModel)
                        .addOnSuccessListener {
                            // After saving, simulate fingerprint submission
                            simulateFingerprintSubmission(childId)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to save child details: ${e.message}", Toast.LENGTH_SHORT).show()
                         }
                }
            }
        }
    }

    private fun simulateFingerprintSubmission(childId: String) {
        // Simulate fingerprint (use a static key for now)
        val simulatedFingerprintKey = "sample-fingerprint-key-12345" // Static fingerprint key for simulation

        // Create a map to update the child node with the fingerprint
        val childUpdates = mapOf(
            "fingerprint" to simulatedFingerprintKey
        )

        // Update child node with simulated fingerprint
        database.child("Families").child(familyId!!).child("children").child(childId).updateChildren(childUpdates)
            .addOnSuccessListener {
                val lostChildDetailsModel = LostChildDetailsModel(childFirstName, childSurName, childFatherName, childFatherPhone )
                // Save lost child information to the LostChildren node
                database.child("LostChildren").child(childId).setValue(lostChildDetailsModel)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Child details saved successfully", Toast.LENGTH_SHORT)
                            .show()
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