package com.example.chattingapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.R
import com.example.chattingapp.activities.ChatRoomActivity
import com.example.chattingapp.models.Room

class FragmentChatsAdapter(var context: Context, var list: ArrayList<Room>) : RecyclerView.Adapter<FragmentChatsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_chats_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        var db: FirebaseFirestore = Firebase.firestore
        holder.textViewName.text = list[position].name
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatRoomActivity::class.java)
            intent.putExtra("roomId", list[position].roomId)
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imageView: ImageView
        var textViewName: TextView
        var textViewMessage: TextView

        init {
            imageView = itemView.findViewById(R.id.fragment_chats_circleImageViewAvatar_item)
            textViewName = itemView.findViewById(R.id.fragment_chats_textViewName_item)
            textViewMessage = itemView.findViewById(R.id.fragment_chats_textViewMessage_item)
        }
    }
}