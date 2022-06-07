package com.example.chattingapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chattingapp.R
import com.example.chattingapp.adapters.ChatRoomActivityAdapter
import com.example.chattingapp.databinding.ActivityChatRoomBinding
import com.example.chattingapp.models.Account
import com.example.chattingapp.models.MessageInRoom
import com.example.chattingapp.models.Room
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatRoomBinding
    private lateinit var listMessage: ArrayList<MessageInRoom>
    private lateinit var chatRoomActivityAdapter: ChatRoomActivityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding()
        listener()
        getAllMessage()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAllMessage() {
        listMessage.clear()
        val roomId: String? = intent.getStringExtra("roomId")
        Firebase.firestore.collection("Room").document(roomId!!).collection("Message").orderBy("time").get()
            .addOnSuccessListener { documents ->
                if (documents != null){
                    for (document in documents){
                        val message: MessageInRoom = document.toObject()
                        listMessage.add(message)
                    }
                    chatRoomActivityAdapter.notifyDataSetChanged()
                }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun listener() {
        binding.imageViewSend.setOnClickListener{
            val text: String = binding.editTextMessage.text.toString()
            if (text.isEmpty()){
                Toast.makeText(this, "Type something...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val roomId: String? = intent.getStringExtra("roomId")
            val doc: DocumentReference = Firebase.firestore.collection("Room").document(roomId!!).collection("Message").document()
            val message = MessageInRoom(doc.id, Firebase.auth.currentUser?.uid, text, Timestamp.now())
            doc.set(message).addOnSuccessListener {
                listMessage.add(message)
                chatRoomActivityAdapter.notifyDataSetChanged()
                binding.editTextMessage.text.clear()
            }.addOnFailureListener {

            }
        }
    }

    private fun viewBinding(){
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        val roomId: String? = intent.getStringExtra("roomId")

        listMessage = ArrayList()
        chatRoomActivityAdapter = ChatRoomActivityAdapter(this, listMessage, roomId!!)
        binding.recyclerViewMessage.setHasFixedSize(true)
        binding.recyclerViewMessage.adapter = chatRoomActivityAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        binding.recyclerViewMessage.layoutManager = linearLayoutManager


        Firebase.firestore.collection("Room").document(roomId!!)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful){
                val room = task.result.toObject<Room>()

                for (userId: String in room?.listUserId!!){
                    if (userId != Firebase.auth.currentUser!!.uid){
                        Firebase.firestore.collection("Account").whereEqualTo("id", userId)
                            .get().addOnCompleteListener {
                                if (it.isSuccessful){
                                    val account = it.result.toObjects<Account>()
                                    binding.textViewName.text = account[0].name
                                    Glide.with(this).load(account[0].img_url).into(binding.circleImageViewAvatar)
                                }
                            }
                    }
                }

            }
        }



    }
}