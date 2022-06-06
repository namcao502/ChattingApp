package com.example.chattingapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.chattingapp.databinding.ActivityRegisterBinding
import com.example.chattingapp.models.Account
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding()
        listener()

    }

    private fun listener() {

        binding.textViewSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.buttonSignUp.setOnClickListener {

            val name: String = binding.editTextName.text.toString()
            val username: String = binding.editTextUsername.text.toString()
            val password: String = binding.editTextPassword.text.toString()

            if (name.isEmpty()){
                Toast.makeText(this, "Name can not be blank!", Toast.LENGTH_SHORT).show()
            }

            if (username.isEmpty()){
                Toast.makeText(this, "Username can not be blank!", Toast.LENGTH_SHORT).show()
            }

            if (password.isEmpty()){
                Toast.makeText(this, "Password can not be blank!", Toast.LENGTH_SHORT).show()
            }

//            val db: FirebaseFirestore = Firebase.firestore
//            val documentReference: DocumentReference = db.collection("Account").document()
//            val account = Account(documentReference.id, name, username, password, "")
//
//            documentReference.set(account).addOnSuccessListener {
//                Toast.makeText(this, "Account is registered!", Toast.LENGTH_SHORT).show()
//            }.addOnFailureListener {
//                Toast.makeText(this, "Error while registering!", Toast.LENGTH_SHORT).show()
//            }

//            auth.createUserWithEmailAndPassword(username, password)
//                .addOnSuccessListener {
//                val account = Account(auth.currentUser?.uid, name, username, password, "")
//                FirebaseFirestore.getInstance().collection("Account").document().set(account)
//                    .addOnSuccessListener {
//                    Toast.makeText(this, "Account is registered!", Toast.LENGTH_SHORT).show()
//                }
//            }.addOnFailureListener {
//                Toast.makeText(this, "Error while registering!", Toast.LENGTH_SHORT).show()
//            }

            Firebase.auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val account = Account(Firebase.auth.currentUser?.uid, name, username, password, "", "")
                        FirebaseFirestore.getInstance().collection("Account").document(Firebase.auth.currentUser!!.uid).set(account)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Account is registered!", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Error while registering!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun viewBinding() {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
    }
}