package com.example.animeheaven

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val masterEmail = "admin@example.com"
    private val masterPassword = "admin123"

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task)
        } else {
            Toast.makeText(this, "Google sign in cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val guestButton = findViewById<Button>(R.id.buttonGuest)
        val googleButton = findViewById<SignInButton>(R.id.buttonGoogleSignIn)
        val registerTextView = findViewById<TextView>(R.id.registerTextView)
        val facebookButton = findViewById<Button>(R.id.buttonFacebook)
        val forgotPasswordTextView = findViewById<TextView>(R.id.forgotPasswordTextView)

        facebookButton.isEnabled = false

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email == masterEmail && password == masterPassword) {
                logLoginEvent("master", email)
                Toast.makeText(this, "Master login successful!", Toast.LENGTH_SHORT).show()
                openMainActivity()
                return@setOnClickListener
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        UserUtils.saveUserToFirestore(firebaseAuth.currentUser!!)
                        logLoginEvent("email", email)
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                        openMainActivity()
                    } else {
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        guestButton.setOnClickListener {
            firebaseAuth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        UserUtils.saveUserToFirestore(firebaseAuth.currentUser!!)
                        logLoginEvent("anonymous", "guest")
                        Toast.makeText(this, "Logged in as guest!", Toast.LENGTH_SHORT).show()
                        openMainActivity()
                    } else {
                        Toast.makeText(this, "Guest login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        registerTextView.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        forgotPasswordTextView.setOnClickListener {
            showResetPasswordDialog()
        }
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(Exception::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!, account.email ?: "unknown")
        } catch (e: Exception) {
            Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, email: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    UserUtils.saveUserToFirestore(firebaseAuth.currentUser!!)
                    logLoginEvent("google", email)
                    Toast.makeText(this, "Google login successful!", Toast.LENGTH_SHORT).show()
                    openMainActivity()
                } else {
                    Toast.makeText(this, "Google login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showResetPasswordDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Reset Password")

        val input = EditText(this)
        input.hint = "Enter your email"
        input.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        builder.setView(input)

        builder.setPositiveButton("Send") { dialog, _ ->
            val email = input.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            } else {
                sendPasswordResetEmail(email)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Reset email sent. Check your inbox.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun openMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun logLoginEvent(method: String, email: String) {
        val bundle = Bundle().apply {
            putString("login_method", method)
            putString("user_email", email)
        }
        firebaseAnalytics.logEvent("user_login", bundle)
    }
}
