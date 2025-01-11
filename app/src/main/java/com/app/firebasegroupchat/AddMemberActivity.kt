package com.app.firebasegroupchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AddMemberActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMemberBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var groupId: String
    private lateinit var adminId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve group ID and admin ID from intent
        groupId = intent.getStringExtra("GROUP_ID").orEmpty()
        adminId = intent.getStringExtra("ADMIN_ID").orEmpty()

        // Validate if the current user is the admin
        if (auth.currentUser?.uid != adminId) {
            Toast.makeText(this, "You are not the admin of this group", Toast.LENGTH_SHORT).show()
            finish()  // If not admin, exit the activity
        }

        // Set up Add Member button
        binding.addMemberButton.setOnClickListener {
            val newMemberEmail = binding.memberEmailEditText.text.toString().trim()
            addMemberToGroup(newMemberEmail)
        }
    }

    private fun addMemberToGroup(email: String) {
        if (email.isNotEmpty()) {
            // Fetch the user by email and add them to the group if they exist
            db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val userId = result.documents[0].id

                        // Add the user to the group
                        db.collection("groups").document(groupId)
                            .update("members", FieldValue.arrayUnion(userId))
                            .addOnSuccessListener {
                                Toast.makeText(this, "Member added successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to add member: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error finding user: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
        }
    }
}
