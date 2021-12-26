package ipca.project.rpglife

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginPage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var LogInButton: Button
    private lateinit var RegisterButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContentView(R.layout.activity_login_page)
        LogInButton = findViewById(R.id.LogInButton)
        RegisterButton = findViewById(R.id.RegisterButton)
        LogInButton.setOnClickListener {
            signIn(
                findViewById<TextView>(R.id.UsernameTextView).text.toString(),
                findViewById<TextView>(R.id.PasswordTextView).text.toString()
            )
        }

        RegisterButton.setOnClickListener {
            createAccount(
                findViewById<TextView>(R.id.UsernameTextView).text.toString(),
                findViewById<TextView>(R.id.PasswordTextView).text.toString()
            )
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }

    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
        // [END create_user_with_email]
    }

    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
        // [END sign_in_with_email]
    }

    private fun updateUI(user: FirebaseUser?) {
        TODO("Not yet implemented")
    }

    private fun reload() {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}