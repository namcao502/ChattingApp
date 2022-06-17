package com.example.chattingapp.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chattingapp.R
import com.example.chattingapp.activities.UserActivity
import com.example.chattingapp.adapters.FragmentChatsAdapter
import com.example.chattingapp.adapters.FragmentChatsPickingUserDialogAdapter
import com.example.chattingapp.models.Account
import com.example.chattingapp.models.MessageInRoom
import com.example.chattingapp.models.Room
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class ChatsFragment : Fragment() {

    lateinit var listRoom: ArrayList<Room>
    lateinit var recyclerViewChat: RecyclerView
    lateinit var fragmentChatsAdapter: FragmentChatsAdapter
    lateinit var imageButtonCreate: ImageButton
    lateinit var circleImageViewAvatar: CircleImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_chats, container, false)

        recyclerViewChat = view.findViewById(R.id.fragment_chats_recyclerView_listChats)
        listRoom = ArrayList()
        fragmentChatsAdapter = FragmentChatsAdapter(requireContext(), listRoom)
        recyclerViewChat.adapter = fragmentChatsAdapter

        imageButtonCreate = view.findViewById(R.id.fragment_chats_ImageButtonEdit)
        circleImageViewAvatar = view.findViewById(R.id.fragment_chats_circleImageViewAvatar)

        loadAvatar()

        listener()

        getAllRoomForCurrentUser()

        return view
    }

    private fun loadAvatar() {
        Firebase.firestore.collection("Account").document(Firebase.auth.currentUser!!.uid)
            .get().addOnCompleteListener {
                if (it.isSuccessful){
                    val account = it.result.toObject<Account>()
                    if (account?.img_url!!.isEmpty()){
                        circleImageViewAvatar.setImageResource(R.drawable.icons8_user_50)
                    }
                    else {
                        Glide.with(requireContext()).load(account.img_url).into(circleImageViewAvatar)
                    }

                }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun listener() {

        circleImageViewAvatar.setOnClickListener {
            startActivity(Intent(requireContext(), UserActivity::class.java))
        }

        imageButtonCreate.setOnClickListener {
            //create dialog for picking user
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.fragment_chats_picking_user_dialog)

            val listUser: ArrayList<Account> = ArrayList()
            val fragmentChatsPickingUserDialogAdapter = FragmentChatsPickingUserDialogAdapter(requireContext(), listUser)
            val recyclerView: RecyclerView = dialog.findViewById(R.id.fragment_chats_picking_user_dialog_recyclerView_listUser)
            recyclerView.adapter = fragmentChatsPickingUserDialogAdapter

            //load all user
            Firebase.firestore.collection("Account").get().addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val account: Account = document.toObject()
                        listUser.add(account)
                    }
                    fragmentChatsPickingUserDialogAdapter.notifyDataSetChanged()
                    dialog.show()
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Can not load!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAllRoomForCurrentUser(){
        listRoom.clear()
        Firebase.firestore.collection("Room")
            .whereArrayContains("listUserId", Firebase.auth.currentUser!!.uid)
            .get().addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val room: Room = document.toObject()
                        listRoom.add(room)
                    }
                    fragmentChatsAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Can not connect!", Toast.LENGTH_SHORT).show()
            }
    }

}
