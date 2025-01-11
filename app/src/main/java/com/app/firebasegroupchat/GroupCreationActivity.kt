package com.app.firebasegroupchat

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.app.firebasegroupchat.databinding.ActivityGroupCreationBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class GroupCreationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupCreationBinding  // ViewBinding reference
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().reference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityGroupCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Handle creating the group
        binding.btnCreateGroup.setOnClickListener {
            val groupName = binding.edtGroupName.text.toString().trim()
            val groupDescription = binding.edtGroupDescription.text.toString().trim()
            val userId = auth.currentUser?.uid

            if (TextUtils.isEmpty(groupName) || TextUtils.isEmpty(groupDescription) || userId == null) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            createGroup(groupName, groupDescription, userId)
        }

        binding.Existinggrp.setOnClickListener{
            startActivity(Intent(this, GroupListActivity::class.java))
        }
    }

    private fun createGroup(groupName: String, groupDescription: String, userId: String) {
        // Show progress bar
        binding.progressBar.visibility = View.VISIBLE

        val groupId = database.child("groups").push().key ?: return

        val group = Group(
            groupId,
            groupName,
            groupDescription,
            userId,
            mapOf(userId to "admin")  // Admin is added by default
        )

        // Store the group in Firebase
        database.child("groups").child(groupId).setValue(group)
            .addOnCompleteListener {
                // Hide progress bar
                binding.progressBar.visibility = View.GONE

                if (it.isSuccessful) {
                    Toast.makeText(this, "Group created successfully!", Toast.LENGTH_SHORT).show()
                    navigateToGroupList()
                } else {
                    Toast.makeText(this, "Failed to create group", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToGroupList() {
        // Navigate to Group List Activity
        startActivity(Intent(this, GroupListActivity::class.java))
    }
}

