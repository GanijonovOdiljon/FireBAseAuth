package com.example.firebaseauth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firebaseauth.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val TAG = "@@@"
    private lateinit var auth: FirebaseAuth
    private lateinit var qso: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        qso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, qso)

        binding.signBtn.setOnClickListener {
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, 1)
        }
        binding.signOut.setOnClickListener {
            googleSignInClient.signOut()
        }
        auth = FirebaseAuth.getInstance()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1)
        {
            val signedInAccountFromIntent = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = signedInAccountFromIntent.getResult(ApiException::class.java)
                firebaceAuthWithGoogle(account?.idToken?: "")
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
    private fun firebaceAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken,null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener (this){task ->
                if (task.isSuccessful){
                    Log.d(TAG, "firebaceAuthWithGoogle: Success")
                    val user = auth.currentUser!!
                    Toast.makeText(this, "Successfully sign in", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "firebase user: $user")
                }else {
                    Log.d(TAG, "signInWithCredential failure: $task ")
                }
            }
    }
}