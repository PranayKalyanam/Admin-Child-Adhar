package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.databinding.ActivityFamilyDetailsBinding
import com.example.myapplication.model.ChildDetailsModel
import com.example.myapplication.model.FamilyDetailsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FamilyDetailsActivity : AppCompatActivity() {

    //Binding View
    private lateinit var binding: ActivityFamilyDetailsBinding

    //Firebase auth and database
    private var auth = FirebaseAuth.getInstance()
    private  var database = FirebaseDatabase.getInstance().reference

    //String to store text from EditTextView
    private  var familyName:String?= null
    private  var familyIncome:String?= null
    private  var familyAddress:String?= null

    //foe child details
    private  var childrenList: MutableList<ChildDetailsModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFamilyDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.familyDetailsSaveButton.setOnClickListener {
            saveFamilyDetails()
        }

    }

    private fun saveFamilyDetails() {
        familyName = binding.familyNameEditText.text.toString().trim()
        familyIncome = binding.familyIncomeEditText.text.toString().trim()
        familyAddress = binding.familyAddressEditText.text.toString().trim()

        if(familyName.isNullOrBlank() || familyIncome.isNullOrBlank() || familyAddress.isNullOrBlank()){
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        } else{
            val userId = auth.currentUser?.uid
            if(userId == null){
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return
            } else{
                // Generate a unique family ID or use pushId
                val familyId = database.child("Families").push().key ?: return
                // Create the family details model
                val familyDetailsModel = FamilyDetailsModel(
                    familyName,
                    familyIncome,
                    familyAddress,
                    childrenList // Add the list of children to the family
                )

                database.child("Families").child(familyId).setValue(familyDetailsModel)
                    .addOnSuccessListener {
                        // Update the user node to associate this family
                        updateUserFamily(userId, familyId)
                    Toast.makeText(this, "Family details saved successfully", Toast.LENGTH_SHORT).show()
                }
                    .addOnFailureListener { e->
                        Toast.makeText(this, "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }

    private fun updateUserFamily(userId: String, familyId: String) {
        val userUpdates = mapOf(
            "familyId" to familyId
        )

        database.child("users").child(userId).updateChildren(userUpdates)
            .addOnSuccessListener {
                Toast.makeText(this, "User linked to family successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}