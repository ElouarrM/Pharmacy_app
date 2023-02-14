package com.example.pharmcy_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.pharmcy_app.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAut: FirebaseAuth;
    private lateinit var database: DatabaseReference;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater);
        setContentView(binding.root)

        binding.textView6.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent);
            finish()
        }

        binding.button.setOnClickListener {
            val firstName = binding.editTextTextPersonName.text.toString()
            val lastName = binding.editTextTextPersonName2.text.toString()
            val adresse = binding.editTextTextPersonName3.text.toString()
            val email = binding.editTextTextEmailAddress2.text.toString()
            val password = binding.editTextNumberPassword.text.toString()
            val Cpassword = binding.editTextNumberPassword2.text.toString()

            var role = ""
            if(binding.RbtnAdmin.isChecked){
                role = binding.RbtnAdmin.text.toString()
            }else{
                role = binding.RbtnClinet.text.toString()
            }


            database = FirebaseDatabase.getInstance().getReference("Users")
            firebaseAut = FirebaseAuth.getInstance()

            val Users = Users(firstName, lastName, adresse, email, password, role)

            if (email.isNotEmpty() && password.isNotEmpty() && Cpassword.isNotEmpty()) {
                if (password == Cpassword) {
                    binding.loading.visibility = View.VISIBLE
                    firebaseAut.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val intent = Intent(this, SignIn::class.java)
                                database.push().setValue(Users).addOnSuccessListener {
                                    binding.editTextTextPersonName.text.clear()
                                    binding.editTextTextPersonName2.text.clear()
                                    binding.editTextTextPersonName3.text.clear()
                                    binding.editTextTextEmailAddress2.text.clear()
                                    binding.editTextNumberPassword.text.clear()
                                    binding.editTextNumberPassword2.text.clear()

                                }
                                startActivity(intent);
                                finish()
                            } else {

                                Toast.makeText(
                                    this,
                                    it.exception.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Aare not allowed", Toast.LENGTH_LONG)
                    .show()
            }


        }
    }
}