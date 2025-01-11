package com.app.firebasegroupchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.firebasegroupchat.databinding.ActivityGroupChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class GroupChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupChatBinding
    private lateinit var messageAdapter: MessageAdapter
    private val messageList = mutableListOf<Message>()
    private val database = FirebaseDatabase.getInstance().reference
    private var groupId: String? = null
    private var currentUserId: String? = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get group ID from intent (assuming you passed it to the activity)
        groupId = intent.getStringExtra("GROUP_ID")

        // Setup RecyclerView
        messageAdapter = MessageAdapter(messageList)
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMessages.adapter = messageAdapter

        // Fetch messages
        listenForMessages(groupId)

        // Send message
        binding.btnSendMessage.setOnClickListener {
            val messageText = binding.etMessage.text.toString()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
            }
        }
    }

    private fun listenForMessages(groupId: String?) {
        groupId?.let {
            database.child("groups").child(it).child("messages")
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        val message = snapshot.getValue(Message::class.java)
                        message?.let {
                            messageList.add(it)
                            messageAdapter.notifyItemInserted(messageList.size - 1)
                        }
                    }

                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onChildRemoved(snapshot: DataSnapshot) {}
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    private fun sendMessage(messageText: String) {
        val message = Message(senderId = currentUserId ?: "", messageText = messageText)

        groupId?.let {
            val messageRef = database.child("groups").child(it).child("messages").push()
            messageRef.setValue(message).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.etMessage.text.clear()
                    Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}










