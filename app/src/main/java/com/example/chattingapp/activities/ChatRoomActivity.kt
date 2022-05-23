package com.example.chattingapp.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chattingapp.adapters.ChatRoomActivityAdapter
import com.example.chattingapp.databinding.ActivityChatRoomBinding
import com.example.chattingapp.models.MessageInRoom
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatRoomBinding
    private lateinit var listMessage: ArrayList<MessageInRoom>
    private lateinit var chatRoomActivityAdapter: ChatRoomActivityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding()
        listener()

        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                getAllMessage()
//                binding.activitiesChatRoomRecyclerViewMessage.smoothScrollToPosition(listMessage.size - 1)
                mainHandler.postDelayed(this, 1000)
            }
        })

    }

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

    private fun listener() {
        binding.activitiesChatRoomCircleImageViewSend.setOnClickListener{
            val text: String = binding.activitiesChatRoomEditTextMessage.text.toString()
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
                binding.activitiesChatRoomEditTextMessage.text.clear()
            }.addOnFailureListener {
            }
        }
    }

    private fun viewBinding(){
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        listMessage = ArrayList()
        chatRoomActivityAdapter = ChatRoomActivityAdapter(this, listMessage)
        binding.activitiesChatRoomRecyclerViewMessage.adapter = chatRoomActivityAdapter
    }
}