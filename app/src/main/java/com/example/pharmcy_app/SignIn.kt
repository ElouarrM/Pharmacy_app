package com.example.pharmcy_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.pharmcy_app.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

class SignIn : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding;
    private lateinit var firebaseAut: FirebaseAuth;

    private fun gotoHome(){
        val intent = Intent(this ,HomeActivity::class.java)
        startActivity(intent);
        finish()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        binding = ActivitySignInBinding.inflate(layoutInflater);
        setContentView(binding.root)
        firebaseAut = FirebaseAuth.getInstance()
        if(firebaseAut.currentUser!= null){
            gotoHome()
        }else{
            setContentView(R.layout.activity_sign_in)
            binding = ActivitySignInBinding.inflate(layoutInflater);
            setContentView(binding.root)

            binding.textView3.setOnClickListener{
                val intent = Intent(this,SignUp::class.java)
                startActivity(intent)
                finish()
            }

            binding.button2.setOnClickListener{
                val email = binding.editTextTextEmailAddress.text.toString()
                val pass = binding.editTextTextPassword.text.toString()
                binding.loading.visibility = View.VISIBLE
                if(email.isNotEmpty() && pass.isNotEmpty()){
                    firebaseAut.signInWithEmailAndPassword(email,pass).addOnCompleteListener{
                        if (it.isSuccessful){
                            gotoHome()
                            binding.loading.visibility = View.GONE
                        }else{
                            Toast.makeText(this,"Mot de passe ou bien email incorrect", Toast.LENGTH_LONG).show()
                            binding.loading.visibility = View.GONE
                        }
                    }
                }
                else{
                    Toast.makeText(this,"Empty Fields are not allowed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}