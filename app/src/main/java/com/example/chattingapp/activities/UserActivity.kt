package com.example.chattingapp.activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.chattingapp.databinding.ActivityUserBinding
import com.example.chattingapp.models.Account
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private var imageFilePath: Uri? = null
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding()
        listener()
    }

    private fun listener() {

        binding.circleImageViewUser.setOnClickListener {
            loadImageFromStorage()
        }

        binding.buttonChangeAvatar.setOnClickListener {
            if (imageUrl != null){
                uploadImageFileToFirestore()
                Firebase.firestore.collection("Account")
                    .document(Firebase.auth.currentUser!!.uid).update("img_url", imageUrl).addOnCompleteListener {
                        Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                    }
            }
            else {
                Toast.makeText(this, "Please pick an image!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonChangeName.setOnClickListener {

            if (binding.editTextName.visibility == View.VISIBLE){

                val name = binding.editTextName.text.toString()
                if (name.isEmpty()){
                    Toast.makeText(this, "Please type a name!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                Firebase.firestore.collection("Account")
                    .document(Firebase.auth.currentUser!!.uid).update("name", name)
                    .addOnCompleteListener {
                        Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                        binding.textViewName.text = name
                    }.addOnFailureListener {
                        Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                    }

                binding.editTextName.visibility = View.INVISIBLE
            }
            else {
                binding.editTextName.visibility = View.VISIBLE
            }

        }

        binding.buttonChangePassword.setOnClickListener {

            if (binding.editTextPassword.visibility == View.VISIBLE){

                val password = binding.editTextPassword.text.toString()

                if (password.isEmpty()){
                    Toast.makeText(this, "Please type a new password!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                Firebase.auth.currentUser!!.updatePassword(password).addOnCompleteListener {
                    Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                }
                binding.editTextPassword.visibility = View.INVISIBLE
            } else {
                binding.editTextPassword.visibility = View.VISIBLE
            }
        }

        binding.buttonSignOut.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun viewBinding() {
        binding = ActivityUserBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        binding.editTextPassword.visibility = View.INVISIBLE
        binding.editTextName.visibility = View.INVISIBLE

        Firebase.firestore.collection("Account").document(Firebase.auth.currentUser!!.uid)
            .get().addOnCompleteListener {
                if (it.isSuccessful){
                    val account = it.result.toObject<Account>()
                    binding.textViewName.text = account?.name ?: "Name"
                    Glide.with(this).load(account?.img_url).into(binding.circleImageViewUser)
                }

        }
    }

    private fun loadImageFromStorage() {
        val intent = Intent()
        intent.type = "Images/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 502)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 502 && resultCode == RESULT_OK && data != null && data.data != null) {
            imageFilePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageFilePath)
                binding.circleImageViewUser.setImageBitmap(bitmap)
                uploadImageFileToFirestore()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImageFileToFirestore() {

        if (imageFilePath != null) {

            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            val ref: StorageReference = FirebaseStorage.getInstance().reference.child("Avatar Images/${Firebase.auth.currentUser!!.uid}")
            ref.putFile(imageFilePath!!)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    ref.downloadUrl.addOnCompleteListener { task ->
                        imageUrl = task.result.toString()
                    }
                    Toast.makeText(this@UserActivity, "Uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(this@UserActivity,"Failed ", Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress: Double = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                    progressDialog.setMessage("Uploading " + progress.toInt() + "%")
                }
        } else {
            Toast.makeText(applicationContext, "Please pick an image", Toast.LENGTH_SHORT).show()
            return
        }
    }
}