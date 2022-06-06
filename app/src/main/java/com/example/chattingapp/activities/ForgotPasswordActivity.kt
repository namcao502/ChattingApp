package com.example.chattingapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.chattingapp.databinding.ActivityForgotPasswordBinding
import com.example.chattingapp.models.Account
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding()
        listener()
    }

    private fun listener() {

        binding.buttonSet.setOnClickListener {

            val username = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (username.isEmpty()){
                Toast.makeText(this, "Username can not be blank!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()){
                Toast.makeText(this, "Password can not be blank!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Firebase.firestore.collection("Account").whereEqualTo("username", username)
                .get().addOnCompleteListener {
                if (it.isSuccessful){
                    val account = it.result.toObjects<Account>()
                    account[0].password = password

                    Firebase.firestore.collection("Account").document(account[0].id!!)
                        .set(account[0]).addOnCompleteListener {
                            

                        }.addOnFailureListener {

                        }

                }
            }
        }
    }

    private fun viewBinding() {
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
    }
}