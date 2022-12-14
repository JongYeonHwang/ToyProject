package com.greedy.toyproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


import com.greedy.toyproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


   private lateinit var auth: FirebaseAuth
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    val helper = SqliteHelper(this, "post", 1)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.join.setOnClickListener {
            startActivity(Intent(this, JoinActivity::class.java))
        }

        binding.btnLogin.setOnClickListener{
            val email = binding.emeil.text.toString()
            val password = binding.password.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) { 
                signIn(email, password)
            } else {
                Toast.makeText(this, "email과 password를 입력해주세요", Toast.LENGTH_SHORT).show()
            }

        }


    }

    private fun signIn(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password) 
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    Toast.makeText(this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show()
                    moveResultPage()
                } else {

                    Toast.makeText(this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()

                }

    }
}

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            moveResultPage()
        }
    }

    fun moveResultPage() { //로그인 성공 or 로그인 된상태일 때
        startActivity(Intent(this, LodingActivity::class.java))
//        startActivity(Intent(this, MypageActivity::class.java))
        finish()
    }


    }
