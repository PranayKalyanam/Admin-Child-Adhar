package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.ChildDetailsAdapter
import com.example.myapplication.databinding.ActivityAddChildrenBinding
import com.example.myapplication.model.ChildDetailsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddChildrenActivity : AppCompatActivity() {

    //Binding View
    private lateinit var binding: ActivityAddChildrenBinding

    //Firebase auth and database
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private var childDetailsModelList: MutableList<ChildDetailsModel> = mutableListOf()
    private var childIdList: MutableList<String> = mutableListOf()

    //the familyId to get child details
    private var familyId: String?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddChildrenBinding.inflate(layoutInflater)
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
                        val intent = Intent(this, FamilyDetailsActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        fetchChildrenDetails()
                    }
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Failed to get familyId", Toast.LENGTH_SHORT).show()
                }
        }

        // Set up the add child button click listener
        binding.addChildButton.setOnClickListener {
            generateChildIdAndNavigate()
        }

    }

    private fun generateChildIdAndNavigate() {
        if(familyId != null){
            // Generate a unique child ID using Firebase push
            val newChildRef = database.child("Families").child(familyId!!).child("children").push()
            val newChildId = newChildRef.key // This is the new unique child ID

            if(newChildId != null){
                // Pass the new child ID to the ChildDetailsActivity
                val intent = Intent(this, ChildDetailsActivity::class.java)
                intent.putExtra("child_id", newChildId) // Send the new child ID
                startActivity(intent)
            } else {
                Toast.makeText(this, "Failed to generate a new child ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchChildrenDetails() {
        // Fetch children data from Firebase under the "Families" node
        if (familyId != null) {
            database.child("Families").child(familyId!!).child("children").get()
                .addOnSuccessListener {
                    // Check if snapshot has data
                    if(it.exists()){
                        childDetailsModelList.clear()  // Clear the list before adding new data
                        childIdList.clear() // Clear the child IDs list as well
                        // Iterate over the snapshot and convert to ChildDetailsModel objects
                        for(childSnapshot in it.children){
                            val child = childSnapshot.getValue(ChildDetailsModel::class.java)
                            child?.let {childDetailsList ->
                                childDetailsModelList.add(childDetailsList) // Add child details to list
                                childIdList.add(childSnapshot.key!!)// Add child ID to childIds list
                            }
                        }
                        // Hide the 'No Info' message and show the RecyclerView
                        binding.addChildDetailsRecyclerView.visibility = android.view.View.VISIBLE
                        binding.noInfoText.visibility = android.view.View.GONE
                        // Now pass the list to the adapter and update RecyclerView
                        setupRecyclerView()
                    } else {
                        // If no data exists, hide the RecyclerView and show the 'No Info' message
                        binding.addChildDetailsRecyclerView.visibility = android.view.View.GONE
                        binding.noInfoText.visibility = android.view.View.VISIBLE
                        Toast.makeText(this, "No children found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Failed to load children data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupRecyclerView() {
        // Set up RecyclerView with a linear layout manager
        binding.addChildDetailsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Create an adapter with the childDetailsModelList
        val adapter = ChildDetailsAdapter(childDetailsModelList, childIdList)

        // Set the adapter for the RecyclerView
        binding.addChildDetailsRecyclerView.adapter = adapter
    }
}