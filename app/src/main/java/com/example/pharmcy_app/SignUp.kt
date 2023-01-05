package com.example.pharmcy_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.pharmcy_app.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding;
    private lateinit var firebaseAut: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        binding= ActivitySignUpBinding.inflate(layoutInflater);
        setContentView(binding.root)

        binding.textView6.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent);
            finish()
        }
        firebaseAut=FirebaseAuth.getInstance()

        binding.button.setOnClickListener{
            val email = binding.editTextTextEmailAddress2.text.toString()
            val pass = binding.editTextNumberPassword.text.toString()
            val passC = binding.editTextNumberPassword2.text.toString()
            if(email.isNotEmpty() && pass.isNotEmpty() && passC.isNotEmpty()){
                if(pass==passC){
                    firebaseAut.createUserWithEmailAndPassword(email,pass).addOnCompleteListener{
                        if (it.isSuccessful){
                            val intent = Intent(this,SignIn::class.java)
                            startActivity(intent);
                            finish()
                        }else{
                            Toast.makeText(this,it.exception.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                }else{
                    Toast.makeText(this,"Password is not matching", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this,"Empty Fields Aare not allowed", Toast.LENGTH_LONG).show()
            }
        }


    }
}