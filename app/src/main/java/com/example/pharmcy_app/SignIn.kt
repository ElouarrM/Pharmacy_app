package com.example.pharmcy_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.pharmcy_app.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignIn : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding;
    private lateinit var firebaseAut: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        binding= ActivitySignInBinding.inflate(layoutInflater);
        setContentView(binding.root)

        binding.textView3.setOnClickListener{
            val intent = Intent(this,SignUp::class.java)
            startActivity(intent)
            finish()
        }
        firebaseAut=FirebaseAuth.getInstance()
        binding.button2.setOnClickListener{
            val email = binding.editTextTextEmailAddress.text.toString()
            val pass = binding.editTextTextPassword.text.toString()
            if(email.isNotEmpty() && pass.isNotEmpty()){
                firebaseAut.signInWithEmailAndPassword(email,pass).addOnCompleteListener{
                        if (it.isSuccessful){
                            val intent = Intent(this,Home::class.java)
                            startActivity(intent);
                            finish()
                        }else{
                            Toast.makeText(this,it.exception.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
            }
            else{
                    Toast.makeText(this,"Empty Fields are not allowed", Toast.LENGTH_LONG).show()
                }

        }

    }

}
