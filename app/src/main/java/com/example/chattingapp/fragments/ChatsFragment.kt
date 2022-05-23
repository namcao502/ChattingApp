package com.example.chattingapp.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.R
import com.example.chattingapp.adapters.FragmentChatsAdapter
import com.example.chattingapp.adapters.FragmentChatsPickingUserDialogAdapter
import com.example.chattingapp.models.Account
import com.example.chattingapp.models.Room
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class ChatsFragment : Fragment() {

    lateinit var listRoom: ArrayList<Room>
    lateinit var recyclerViewChat: RecyclerView
    lateinit var fragmentChatsAdapter: FragmentChatsAdapter
    lateinit var imageButtonCreate: ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_chats, container, false)

        recyclerViewChat = view.findViewById(R.id.fragment_chats_recyclerView_listChats)
        listRoom = ArrayList()
        fragmentChatsAdapter = FragmentChatsAdapter(requireContext(), listRoom)
        recyclerViewChat.adapter = fragmentChatsAdapter

        imageButtonCreate = view.findViewById(R.id.fragment_chats_ImageButtonEdit)

        listener()

        getAllRoomForCurrentUser()

        return view
    }

    private fun listener() {
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

    private fun getAllRoomForCurrentUser(){
        listRoom.clear()
        Firebase.firestore.collection("Room").whereArrayContains("listUserId", Firebase.auth.currentUser!!.uid)
            .get().addOnSuccessListener { documents ->
                if (documents != null){
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