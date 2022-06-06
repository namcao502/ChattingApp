package com.example.chattingapp.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.chattingapp.R
import com.example.chattingapp.databinding.ActivityHomeBinding
import com.example.chattingapp.fragments.ChatsFragment
import com.example.chattingapp.fragments.PeopleFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding()
        listener()
        loadFragment(ChatsFragment())
    }

    private fun listener() {
        binding.navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_chats -> {
                    loadFragment(ChatsFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_people -> {
                    loadFragment(PeopleFragment())
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.home_container, fragment)
        transaction.addToBackStack(fragment.javaClass.simpleName)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }

    private fun viewBinding() {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
    }
}

