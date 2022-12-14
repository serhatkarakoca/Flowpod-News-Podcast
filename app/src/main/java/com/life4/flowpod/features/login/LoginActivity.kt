package com.life4.flowpod.features.login

import android.app.Activity
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.life4.core.core.view.BaseActivity
import com.life4.core.core.vm.BaseViewModel
import com.life4.core.extensions.move
import com.life4.core.extensions.observe
import com.life4.flowpod.R
import com.life4.flowpod.data.MyPreference
import com.life4.flowpod.databinding.ActivityLoginBinding
import com.life4.flowpod.databinding.BottomSheetResetPasswordBinding
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

        observe(viewModel.signInMode) {
            getBinding().isSignIn = viewModel.signInMode.value
        }

        auth = FirebaseAuth.getInstance()
        auth.useAppLanguage()

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


    }

    override fun setupListener() {

        getBinding().buttonSignIn.setOnClickListener {
            googleSignInClient.signOut()
            signIn()
        }

        getBinding().btnLogin.setOnClickListener {
            if (isValid()) {
                getBinding().emailLayout.isErrorEnabled = false
                getBinding().passwordLayout.isErrorEnabled = false
                viewModel.baseLiveData.value = BaseViewModel.State.ShowLoading()
                if (viewModel.signInMode.value == true) {
                    auth.signInWithEmailAndPassword(
                        getBinding().etEmail.text.toString(),
                        getBinding().etPassword.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            myPref.setUsername(getBinding().etEmail.text.toString())
                            move(MainActivity::class.java)
                        } else {
                            viewModel.baseLiveData.value = BaseViewModel.State.ShowContent()
                            showInfoDialog(getString(R.string.incorrect_email_password))
                        }
                    }
                } else {
                    auth.createUserWithEmailAndPassword(
                        getBinding().etEmail.text.toString(),
                        getBinding().etPassword.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            myPref.setUsername(getBinding().etEmail.text.toString())
                            move(MainActivity::class.java)
                        } else {
                            viewModel.baseLiveData.value = BaseViewModel.State.ShowContent()
                            if (it.exception?.message?.lowercase()
                                    ?.contains("badly formatted") == true
                            ) {
                                showInfoDialog(getString(R.string.email_badly_format))
                            } else if (it.exception?.message?.lowercase()
                                    ?.contains("already in use") == true
                            ) {
                                showInfoDialog(getString(R.string.email_already_use))
                            }
                        }
                    }
                }
            }
        }

        getBinding().tvSignInUp.setOnClickListener {
            viewModel.signInMode.value?.let {
                viewModel.signInMode.value = !it
            }
        }

        getBinding().tvResetPassword.setOnClickListener {
            BottomSheetDialog(this).apply {
                val inflater = LayoutInflater.from(this@LoginActivity)
                val binding = DataBindingUtil.inflate<BottomSheetResetPasswordBinding>(
                    inflater,
                    R.layout.bottom_sheet_reset_password,
                    null,
                    false
                )
                binding.btnLogin.setOnClickListener {
                    if (isValidEmail(binding.etEmail.text.toString())) {
                        auth.sendPasswordResetEmail(binding.etEmail.text.toString())
                            .addOnCompleteListener {
                                showInfoDialog(
                                    getString(R.string.success_reset_password), getString(
                                        R.string.success
                                    )
                                )
                            }
                        dismiss()
                    } else {
                        binding.emailLayout.isErrorEnabled = true
                        binding.emailLayout.error = getString(R.string.invalid_email)
                    }
                }
                setContentView(binding.root)
            }.show()
        }
    }

    private fun showInfoDialog(message: String, title: String? = null) {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(message)
            setTitle(title ?: getString(R.string.warning))
            setPositiveButton(getString(R.string.ok), null)
        }.show()
    }

    private fun isValid(): Boolean {
        val email = getBinding().etEmail.text.toString().trim()
        val password = getBinding().etPassword.text.toString().trim()
        return if (email.isEmpty()) {
            getBinding().passwordLayout.isErrorEnabled = false
            getBinding().emailLayout.isErrorEnabled = true
            getBinding().emailLayout.error = getString(R.string.empty_field)
            false
        } else if (!isValidEmail(email)) {
            getBinding().passwordLayout.isErrorEnabled = false
            getBinding().emailLayout.isErrorEnabled = true
            getBinding().emailLayout.error = getString(R.string.invalid_email)
            return false
        } else if (password.isEmpty()) {
            getBinding().emailLayout.isErrorEnabled = false
            getBinding().passwordLayout.isErrorEnabled = true
            getBinding().passwordLayout.error = getString(R.string.empty_field)
            false
        } else if (password.length < 6) {
            getBinding().emailLayout.isErrorEnabled = false
            getBinding().passwordLayout.isErrorEnabled = true
            getBinding().passwordLayout.error = getString(R.string.invalid_password_length)
            return false
        } else true
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
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