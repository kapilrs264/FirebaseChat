package com.app.firebasegroupchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class InviteUsersActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddSelectedUsers: Button
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var usersList: List<User>
    private lateinit var selectedUsers: MutableList<User>

    private lateinit var groupId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_users)

        // Get groupId from the intent
        groupId = intent.getStringExtra("GROUP_ID") ?: ""

        recyclerView = findViewById(R.id.recyclerViewUsers)
        btnAddSelectedUsers = findViewById(R.id.btnAddSelectedUsers)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        selectedUsers = mutableListOf()

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch all users from Firebase
        fetchUsers()

        // Handle adding selected users to the group
        btnAddSelectedUsers.setOnClickListener {
            addUsersToGroup()
        }
    }

    private fun fetchUsers() {
        // Fetch all users (excluding the current user)
        val currentUserId = auth.currentUser?.uid ?: return

        database.child("users").get().addOnSuccessListener { snapshot ->
            usersList = snapshot.children.mapNotNull {
                val user = it.getValue(User::class.java)
                if (user != null && it.key != currentUserId) {
                    user
                } else {
                    null
                }
            }
            // Set up RecyclerView with users list
            recyclerView.adapter = UsersAdapter(usersList, ::onUserSelected)
        }
    }

    private fun onUserSelected(user: User, isSelected: Boolean) {
        if (isSelected) {
            selectedUsers.add(user)
        } else {
            selectedUsers.remove(user)
        }
    }

    private fun addUsersToGroup() {
        if (selectedUsers.isNotEmpty()) {
            val groupRef = database.child("groups").child(groupId).child("members")
            for (user in selectedUsers) {
                groupRef.child(user.id).setValue("member") // Add as member
            }

            Toast.makeText(this, "Users added to the group!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No users selected", Toast.LENGTH_SHORT).show()
        }
    }
}

