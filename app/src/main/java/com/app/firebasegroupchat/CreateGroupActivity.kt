package com.app.firebasegroupchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.app.firebasegroupchat.databinding.ActivityCreateGroupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class CreateGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateGroupBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up create group button click listener
        binding.createGroupButton.setOnClickListener {
            createGroup()
        }
    }

    private fun createGroup() {
        val groupName = binding.groupNameEditText.text.toString().trim()
        val groupDescription = binding.groupDescriptionEditText.text.toString().trim()

        if (groupName.isNotEmpty() && groupDescription.isNotEmpty()) {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

            val group = hashMapOf(
                "groupName" to groupName,
                "groupDescription" to groupDescription,
                "adminId" to currentUserId,  // The user who creates the group is the admin
                "members" to listOf(currentUserId)  // Initially, the admin is the only member
            )

            db.collection("groups")
                .add(group)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this, "Group created successfully", Toast.LENGTH_SHORT).show()

                    // After creating the group, navigate to the GroupListActivity
                    val intent = Intent(this, GroupListActivity::class.java)
                    startActivity(intent)
                    finish() // Close CreateGroupActivity
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error creating group: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please enter a valid group name and description", Toast.LENGTH_SHORT).show()
        }
    }

}
