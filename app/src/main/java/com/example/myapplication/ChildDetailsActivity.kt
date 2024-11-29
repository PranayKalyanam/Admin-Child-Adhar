package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.databinding.ActivityChildDetailsBinding
import com.example.myapplication.model.ChildDetailsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Suppress("DEPRECATION")
class ChildDetailsActivity : AppCompatActivity() {
    //Binding View
    private lateinit var binding: ActivityChildDetailsBinding

    //Firebase auth and database
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    //the familyId to save child details
    private var familyId: String?= null

    // Get the child ID passed from AddChildrenActivity
    private var childId: String? = null

    //Strings to store from edit text
    private var childFirstName:String?=null
    private var childSurName:String?=null
    private var childFatherName:String?=null
    private var childMotherName:String?=null
    private var childFatherPhone: Int?= null
    private var childDateOfBirth:String?=null
    private var childTimeOfBirth:String?=null
    private var childPlaceOfBirth:String?=null
    private var childGender:String?=null
    private var childDisability:String?=null
    private var childAddress:String?=null

    private var childDetails: ChildDetailsModel?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChildDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the child ID and child details passed via Intent
        childId = intent.getStringExtra("child_id")
        childDetails = intent.getParcelableExtra("child_details_model")

        // Check if the data is not null
        if(childDetails != null){
            // Display the child details in the UI
            binding.childFirstNameEditText.setText(childDetails!!.firstName)
            binding.childSurnameEditText.setText(childDetails!!.lastName)
            binding.childFatherNameEditText.setText(childDetails!!.fatherName)
            binding.childMotherNameEditText.setText(childDetails!!.motherName)
            binding.childFatherPhoneEditText.setText(childDetails!!.fatherPhone.toString())
            binding.childDateOfBirthEditText.setText(childDetails!!.dateOfBirth)
            binding.childPlaceOfBirthEditText.setText(childDetails!!.placeOfBirth)
            binding.childTimeOfBirthEditText.setText(childDetails!!.timeOfBirth)
            binding.childGenderEditText.setText(childDetails!!.gender)
            binding.childDisabilityEditText.setText(childDetails!!.disability)
            binding.childAddressEditText.setText(childDetails!!.permanentAddressOfParents)
        }


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

        if(childId != null){
            binding.childDetailsSaveButton.setOnClickListener {
                saveChildDetails()
            }
        } else {
                Toast.makeText(this, "No Child ID received", Toast.LENGTH_SHORT).show()
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
                if(childId != null){
                    database.child("Families").child(familyId!!).child("children").child(childId!!).setValue(childDetailsModel)
                        .addOnSuccessListener {

                            if(childDetails != null){
                                Toast.makeText(this, "Saved child details successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                // After saving, navigate to the fingerprint activity
                                navigateToFingerprintActivity(childId!!, familyId!!)
                            }

                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to save child details: ${e.message}", Toast.LENGTH_SHORT).show()
                         }
                }
            }
        }
    }

    private fun navigateToFingerprintActivity(childId: String, familyId: String) {
        // Start FingerprintActivity and pass the child ID for further use
        val intent =  Intent(this, FingerPrintActivity::class.java)
        intent.putExtra("child_id", childId)
        intent.putExtra("family_id", familyId)
        intent.putExtra("child_first_name", this.childFirstName)
        intent.putExtra("child_last_name", this.childSurName)
        intent.putExtra("father_name", this.childFatherName)
        intent.putExtra("mother_name", this.childMotherName)
        intent.putExtra("father_phone", this.childFatherPhone)
        startActivity(intent)
    }

}