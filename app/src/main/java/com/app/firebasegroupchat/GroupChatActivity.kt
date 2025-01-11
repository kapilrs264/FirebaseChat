package com.app.firebasegroupchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.firebasegroupchat.databinding.ActivityGroupChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class GroupChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupChatBinding
    private val db = FirebaseFirestore.getInstance()
    private val groupId: String? by lazy { intent.getStringExtra("GROUP_ID") }

    private lateinit var messagesAdapter: MessagesAdapter
    private val messagesList = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the RecyclerView and Adapter
        messagesAdapter = MessagesAdapter(messagesList, FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.messagesRecyclerView.adapter = messagesAdapter

        // Load group messages
        loadMessages()

        // Set up Send Message Button
        binding.sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun loadMessages() {
        // Listen for real-time updates from Firestore (messages in this group)
        db.collection("messages")
            .whereEqualTo("groupId", groupId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("GroupChatActivity", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    messagesList.clear() // Clear the list before adding new data
                    for (document in snapshot.documents) {
                        val message = document.toObject(Message::class.java)
                        message?.let { messagesList.add(it) }
                    }

                    // Notify the adapter that the data has changed
                    messagesAdapter.notifyDataSetChanged()

                    // Scroll to the last message in the list
                    if (messagesList.isNotEmpty()) {
                        binding.messagesRecyclerView.scrollToPosition(messagesList.size - 1)
                    }
                }
            }
    }





    private fun sendMessage() {
        val messageText = binding.messageEditText.text.toString().trim()

        if (messageText.isNotEmpty()) {
            // Get the current user ID
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

            // Create the message object
            val message = Message(
                text = messageText,
                senderId = currentUserId,
                timestamp = System.currentTimeMillis(),
                groupId = groupId.orEmpty()
            )

            // Add the message to Firestore
            db.collection("messages").add(message)
                .addOnSuccessListener { documentReference ->
                    // Clear the message input field
                    binding.messageEditText.text!!.clear()

                    // Add the message to the local list and notify the adapter
                    messagesList.add(message) // Adding the sent message to the list
                    messagesAdapter.notifyItemInserted(messagesList.size - 1) // Notify the adapter that a new item was inserted
                    binding.messagesRecyclerView.scrollToPosition(messagesList.size - 1) // Scroll to the last message

                    // Show success toast
                    Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to send message: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }





}
