package com.example.chattingapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.R
import com.example.chattingapp.activities.ChatRoomActivity
import com.example.chattingapp.models.MessageInRoom
import java.text.SimpleDateFormat

class ChatRoomActivityAdapter(var context: ChatRoomActivity, var list: ArrayList<MessageInRoom>) : RecyclerView.Adapter<ChatRoomActivityAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.activity_chat_room_from_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewMessage.text = list[position].content
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        val time = simpleDateFormat.format(list[position].time?.toDate()?.time)
        holder.textViewTime.text = time
    }


    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imageView: ImageView
        var textViewTime: TextView
        var textViewMessage: TextView

        init {
            imageView = itemView.findViewById(R.id.activity_chat_room_circleImageViewAvatar_item)
            textViewTime = itemView.findViewById(R.id.activity_chat_room_textViewTime_item)
            textViewMessage = itemView.findViewById(R.id.activity_chat_room_textViewMessage_item)
        }
    }

}