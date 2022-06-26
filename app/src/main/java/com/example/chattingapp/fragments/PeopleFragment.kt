package com.example.chattingapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.R
import com.example.chattingapp.adapters.FragmentPeopleAdapter
import com.example.chattingapp.models.Account
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class PeopleFragment : Fragment() {

    lateinit var editTextSearch: EditText
    lateinit var buttonSearch: Button
    lateinit var recyclerViewPeople: RecyclerView
    lateinit var listPeople: ArrayList<Account>
    lateinit var fragmentPeopleAdapter: FragmentPeopleAdapter


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_people, container, false)

        editTextSearch = view.findViewById(R.id.fragment_people_editTextTextSearch)
        buttonSearch = view.findViewById(R.id.fragment_people_buttonSearch)
        recyclerViewPeople = view.findViewById(R.id.fragment_people_recyclerViewPeople)
        listPeople = ArrayList()
        fragmentPeopleAdapter = FragmentPeopleAdapter(requireContext(), listPeople)
        recyclerViewPeople.adapter = fragmentPeopleAdapter

        loadAllPeople()

        buttonSearch.setOnClickListener {
            listPeople.clear()
            val text: String = editTextSearch.text.toString()
            if (text.isEmpty()){
                Toast.makeText(requireContext(), "Please type a name to find", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Firebase.firestore.collection("Account").whereEqualTo("name", text).get().addOnCompleteListener {
                if (it.isSuccessful){
                    for (document in it.result){
                        val account: Account = document.toObject()
                        listPeople.add(account)
                    }
                    fragmentPeopleAdapter.notifyDataSetChanged()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Sorry, $text is not here", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadAllPeople() {
        Firebase.firestore.collection("Account").get().addOnCompleteListener {
            if (it.isSuccessful){
                for (document in it.result){
                    val account: Account = document.toObject()
                    listPeople.add(account)
                }
                fragmentPeopleAdapter.notifyDataSetChanged()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Can not connect!", Toast.LENGTH_SHORT).show()
        }
    }

}