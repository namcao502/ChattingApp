package com.example.chattingapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chattingapp.R
import com.example.chattingapp.activities.ChatRoomActivity
import com.example.chattingapp.models.Account
import com.example.chattingapp.models.Room
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class FragmentPeopleAdapter(var context: Context, var list: ArrayList<Account>)
    : RecyclerView.Adapter<FragmentPeopleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_people_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        var db: FirebaseFirestore = Firebase.firestore

        holder.textViewName.text = list[position].name

        if (list[position].img_url!!.isEmpty()){
            holder.imageView.setImageResource(R.drawable.icons8_user_50)
        }
        else {
            Glide.with(context).load(list[position].img_url).into(holder.imageView)
        }

        holder.itemView.setOnClickListener {
            //create a new room
            val doc: DocumentReference = Firebase.firestore.collection("Room").document()

            val listUser: ArrayList<String> = ArrayList()
            listUser.add(Firebase.auth.currentUser!!.uid)
            listUser.add(list[position].id!!)

            val room = Room(doc.id, list[position].name, listUser)

            doc.set(room).addOnSuccessListener {
                //go to chat room
                val intent = Intent(context, ChatRoomActivity::class.java)
                intent.putExtra("roomId", doc.id)
                context.startActivity(intent)
                Toast.makeText(context, "Created a chat!", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener {
                Toast.makeText(context, "Can not create a chat!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imageView: CircleImageView
        var textViewName: TextView

        init {
            imageView = itemView.findViewById(R.id.fragment_people_circleImageViewAvatar_item)
            textViewName = itemView.findViewById(R.id.fragment_people_textViewName_item)
        }
    }
}