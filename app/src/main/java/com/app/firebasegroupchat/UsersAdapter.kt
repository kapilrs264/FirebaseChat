package com.app.firebasegroupchat

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class UsersAdapter(private val users: List<User>, private val onItemSelected: (User, Boolean) -> Unit) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    private val selectedUsers = mutableSetOf<User>()

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): UserViewHolder {
        val view = android.view.LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_multiple_choice, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.itemView.findViewById<android.widget.TextView>(android.R.id.text1).text = user.username
        holder.itemView.setOnClickListener {
            val isSelected = !selectedUsers.contains(user)
            if (isSelected) selectedUsers.add(user) else selectedUsers.remove(user)
            onItemSelected(user, isSelected)
        }
        holder.itemView.isSelected = selectedUsers.contains(user)
    }

    override fun getItemCount(): Int = users.size

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view)
}