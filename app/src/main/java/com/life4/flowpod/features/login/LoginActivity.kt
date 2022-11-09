package com.life4.flowpod.features.login

import android.app.Activity
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.life4.core.core.view.BaseActivity
import com.life4.core.extensions.move
import com.life4.flowpod.R
import com.life4.flowpod.data.MyPreference
import com.life4.flowpod.databinding.ActivityLoginBinding
import com.life4.flowpod.features.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {
    private val viewModel: LoginViewModel by viewModels()

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    @Inject
    lateinit var myPref: MyPreference

    override fun setupDefinition(savedInstanceState: Bundle?) {
        setupViewModel(viewModel)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val paint = getBinding().tvWelcome.paint
        val width = paint.measureText(getBinding().tvWelcome.text.toString())
        getBinding().tvWelcome.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            val textShader: Shader = LinearGradient(
                0f, 0f, width * 0.2f, getBinding().tvWelcome.height.toFloat(), intArrayOf(
                    Color.parseColor("#B36CEA"),
                    Color.parseColor("#ED9F67")
                ), null, Shader.TileMode.CLAMP
            )

            getBinding().tvWelcome.paint.shader = textShader
        }

        getBinding().buttonSignIn.setOnClickListener {
            googleSignInClient.signOut()
            signIn()
        }

    }

    fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                myPref.setUsername(FirebaseAuth.getInstance().currentUser?.email)
                move(MainActivity::class.java)
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

}