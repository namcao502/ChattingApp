package com.example.chattingapp.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chattingapp.R
import com.example.chattingapp.activities.ChatRoomActivity
import com.example.chattingapp.models.Account
import com.example.chattingapp.models.MessageInRoom
import com.example.chattingapp.models.Room
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class ChatRoomActivityAdapter(var context: ChatRoomActivity, private var list: ArrayList<MessageInRoom>, var roomId: String) : RecyclerView.Adapter<ChatRoomActivityAdapter.ViewHolder>() {

    private var SEND: Int = 0
    private var RECEIVE: Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == SEND){
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.activity_chat_room_to_item, parent, false)
            ViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.activity_chat_room_from_item, parent, false)
            ViewHolder(view)
        }
    }

    @SuppressLint("SimpleDateFormat", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Firebase.firestore.collection("Account").document(list[position].userId!!)
            .get().addOnCompleteListener {
            if (it.isSuccessful){
                val account = it.result.toObject<Account>()
                Glide.with(context).load(account?.img_url).into(holder.imageView)
            }
        }

        holder.textViewMessage.text = list[position].content
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        val time = simpleDateFormat.format(list[position].time?.toDate()?.time)
        holder.textViewTime.text = time

        holder.itemView.setOnLongClickListener {
            val alertDialog = AlertDialog.Builder(it.rootView.context)
            alertDialog.setIcon(R.drawable.icons8_delete_chat_50)
            alertDialog.setTitle("You really want to delete this?")
            alertDialog.setCancelable(false)

            alertDialog.setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
                if (Firebase.auth.currentUser?.uid.equals(list[position].userId)){
                    Firebase.firestore.collection("Room").document(roomId).collection("Message").document(list[position].messageId!!)
                        .delete().addOnCompleteListener {
                            Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show()
                            list.removeAt(position)
                            this.notifyDataSetChanged()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show()
                        }
                }
                else {
                    Toast.makeText(context, "You can not delete this!", Toast.LENGTH_SHORT).show()
                }

            }
            alertDialog.setNegativeButton("No") { _: DialogInterface?, _: Int ->

            }
            alertDialog.show()
            return@setOnLongClickListener false
        }
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

    override fun getItemViewType(position: Int): Int {
        return if (Firebase.auth.currentUser!!.uid == list[position].userId){
            SEND
        } else
            RECEIVE
    }

}