package com.app.firebasegroupchat

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.firebasegroupchat.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var messagesAdapter: MessagesAdapter
    private val messagesList = mutableListOf<Message>()

    private val db = FirebaseFirestore.getInstance()
    private val groupId = "group1"  // You can dynamically set this based on the selected group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout using ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get current user ID
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

        // Set up RecyclerView and Adapter
        messagesAdapter = MessagesAdapter(messagesList, currentUserId)
        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.messagesRecyclerView.adapter = messagesAdapter

        // Set up the Send button click listener
        binding.sendButton.setOnClickListener { sendMessage() }

        // Load messages for the group and listen for real-time updates
        loadMessages()
    }

    private fun loadMessages() {
        // Listen for real-time updates from Firestore
        db.collection("messages")
            .whereEqualTo("groupId", groupId)  // Filter messages by groupId
            .orderBy("timestamp", Query.Direction.ASCENDING)  // Order by timestamp
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("MainActivity", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Clear the current list to avoid duplicates and add the new messages
                    messagesList.clear()

                    // Iterate through each document in the snapshot
                    for (document in snapshot.documents) {
                        val message = document.toObject(Message::class.java)
                        message?.let { messagesList.add(it) }
                    }

                    // Notify the adapter to update the RecyclerView with the new messages
                    messagesAdapter.notifyDataSetChanged()

                    // Scroll to the bottom of the RecyclerView when new messages are loaded
                    binding.messagesRecyclerView.scrollToPosition(messagesList.size - 1)
                }
            }
    }

    private fun sendMessage() {
        val messageText = binding.messageEditText.text.toString().trim()

        if (messageText.isNotEmpty()) {
            // Create a new message object
            val message = Message(
                text = messageText,
                senderId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty(),
                timestamp = System.currentTimeMillis(),
                groupId = groupId  // Assign the groupId
            )

            // Add the new message to Firestore
            db.collection("messages").add(message)
                .addOnSuccessListener {
                    // Clear the message input field after sending the message
                    binding.messageEditText.text.clear()

                    // Add the message to the local list to show in RecyclerView
                    messagesList.add(message)

                    // Notify the adapter that data has changed
                    messagesAdapter.notifyItemInserted(messagesList.size - 1)

                    // Scroll to the bottom to show the latest message
                    binding.messagesRecyclerView.scrollToPosition(messagesList.size - 1)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Message sending failed", Toast.LENGTH_SHORT).show()
                }
        }
    }
}







