package com.app.firebasegroupchat

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity


import android.content.Intent
import android.os.Bundle
import android.widget.Toast

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.firebasegroupchat.databinding.ActivityGroupListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*




class GroupListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupListBinding // Use ViewBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var groupList: MutableList<Group>
    private lateinit var groupAdapter: GroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityGroupListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        groupList = mutableListOf()
        groupAdapter = GroupAdapter(groupList) { group ->
            // When a group is clicked, navigate to GroupChatActivity
            val intent = Intent(this, GroupChatActivity::class.java)
            intent.putExtra("GROUP_ID", group.groupId) // Pass groupId to the chat activity
            startActivity(intent)
        }

        binding.recyclerViewGroups.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewGroups.adapter = groupAdapter

        // Fetch all groups from Firebase
        fetchAllGroups()
    }

    private fun fetchAllGroups() {
        // Fetch all groups, even those the current user is not a member of
        database.child("groups").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                groupList.clear()  // Clear the previous data

                // Iterate through all groups in the "groups" node
                for (groupSnapshot in snapshot.children) {
                    val group = groupSnapshot.getValue(Group::class.java)
                    if (group != null) {
                        groupList.add(group)  // Add each group to the list
                    }
                }

                // Notify the adapter that the data has changed
                groupAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@GroupListActivity, "Failed to load groups", Toast.LENGTH_SHORT).show()
            }
        })
    }
}



