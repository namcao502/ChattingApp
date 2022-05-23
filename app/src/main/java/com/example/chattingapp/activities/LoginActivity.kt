package com.example.chattingapp.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chattingapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferencesRemember: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding()
        listener()
    }

    private fun viewBinding() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view : View = binding.root
        setContentView(view)

        //get data from sharedReference
        sharedPreferencesRemember = getSharedPreferences("login_data", MODE_PRIVATE)
        binding.editTextUsername.setText(sharedPreferencesRemember.getString("username", ""))
        binding.editTextPassword.setText(sharedPreferencesRemember.getString("password", ""))
        binding.checkBoxRemember.isChecked = sharedPreferencesRemember.getBoolean("check", false)
    }

    private fun listener() {
        binding.buttonSignIn.setOnClickListener {

            val username: String = binding.editTextUsername.text.toString()
            val password: String = binding.editTextPassword.text.toString()

            if (username.isEmpty()){
                Toast.makeText(this, "Username can not be blank!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()){
                Toast.makeText(this, "Password can not be blank!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            val db: FirebaseFirestore = Firebase.firestore
//            val documentReference: Query = db.collection("Account").whereEqualTo("username", username)
//            documentReference.get().addOnSuccessListener { document ->
//                    if (document != null){
//                        val account: List<Account> = document.toObjects()
//                        if (account[0].password == password){
//                            if (binding.checkBoxRemember.isChecked){
//                                val editor: SharedPreferences.Editor = sharedPreferencesRemember.edit()
//                                editor.putString("username", username)
//                                editor.putString("password", password)
//                                editor.putBoolean("check", true)
//                                editor.apply()
//                            }
//                            else {
//                                val editor: SharedPreferences.Editor = sharedPreferencesRemember.edit()
//                                editor.putString("username", "")
//                                editor.putString("password", "")
//                                editor.putBoolean("check", false)
//                                editor.apply()
//                            }
//                            Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show()
//                            startActivity(Intent(this, HomeActivity::class.java))
//                        }
//                        else {
//                            Toast.makeText(this, "Wrong password!", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                    else {
//                        Toast.makeText(this, "Can not connect!", Toast.LENGTH_SHORT).show()
//                    }
//            }
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            auth.signInWithEmailAndPassword(username, password).addOnSuccessListener {
                if (binding.checkBoxRemember.isChecked){
                    val editor: SharedPreferences.Editor = sharedPreferencesRemember.edit()
                    editor.putString("username", username)
                    editor.putString("password", password)
                    editor.putBoolean("check", true)
                    editor.apply()
                }
                else {
                    val editor: SharedPreferences.Editor = sharedPreferencesRemember.edit()
                    editor.putString("username", "")
                    editor.putString("password", "")
                    editor.putBoolean("check", false)
                    editor.apply()
                }
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
            }.addOnFailureListener {
                Toast.makeText(this, "Can not connect!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.textViewSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}