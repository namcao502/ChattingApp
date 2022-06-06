package com.example.chattingapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
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
import com.example.chattingapp.models.Room
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class FragmentChatsAdapter(var context: Context, var list: ArrayList<Room>)
    : RecyclerView.Adapter<FragmentChatsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_chats_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        var db: FirebaseFirestore = Firebase.firestore
//        holder.textViewName.text = list[position].name

        for (userId: String in list[position].listUserId!!){
            if (userId != Firebase.auth.currentUser!!.uid){
                Firebase.firestore.collection("Account").whereEqualTo("id", userId)
                    .get().addOnCompleteListener {
                    if (it.isSuccessful){
                        val account = it.result.toObjects<Account>()
                        holder.textViewName.text = account[0].name
                        Glide.with(context).load(account[0].img_url).into(holder.imageView)
                    }
                }
            }
            else {
                holder.imageView.setImageResource(R.drawable.icons8_user_50)
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatRoomActivity::class.java)
            intent.putExtra("roomId", list[position].roomId)
            context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {

            val alertDialog = AlertDialog.Builder(it.rootView.context)
            alertDialog.setIcon(R.drawable.icons8_delete_chat_50)
            alertDialog.setTitle("You really want to delete this?")
            alertDialog.setCancelable(false)

            alertDialog.setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
                Firebase.firestore.collection("Room").document(list[position].roomId!!)
                    .delete().addOnCompleteListener {
                        Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show()
                        this.notifyDataSetChanged()
                }
                    .addOnFailureListener {
                        Toast.makeText(context, "Can not delete this chat!", Toast.LENGTH_SHORT).show()
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
        var textViewName: TextView
        var textViewMessage: TextView

        init {
            imageView = itemView.findViewById(R.id.fragment_chats_circleImageViewAvatar_item)
            textViewName = itemView.findViewById(R.id.fragment_chats_textViewName_item)
            textViewMessage = itemView.findViewById(R.id.fragment_chats_textViewMessage_item)
        }
    }
}