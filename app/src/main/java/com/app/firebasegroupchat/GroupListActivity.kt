package com.app.firebasegroupchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.firebasegroupchat.databinding.ActivityGroupListBinding
import com.google.firebase.firestore.FirebaseFirestore

class GroupListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupListBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var groupsAdapter: GroupsAdapter
    private val groupsList = mutableListOf<Group>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the RecyclerView and Adapter for groups
        groupsAdapter = GroupsAdapter(groupsList) { group ->
            // When a group is clicked, open the GroupChatActivity
            val intent = Intent(this, GroupChatActivity::class.java)
            intent.putExtra("GROUP_ID", group.id)
            startActivity(intent)
        }

        binding.groupsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.groupsRecyclerView.adapter = groupsAdapter

        // Load the groups from Firestore
        loadGroups()
    }

    private fun loadGroups() {
        db.collection("groups")
            .get()
            .addOnSuccessListener { snapshot ->
                groupsList.clear()
                for (document in snapshot.documents) {
                    val group = document.toObject(Group::class.java)?.apply {
                        id = document.id
                    }
                    group?.let { groupsList.add(it) }
                }
                groupsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load groups: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
