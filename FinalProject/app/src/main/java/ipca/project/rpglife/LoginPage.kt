package ipca.project.rpglife

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginPage : AppCompatActivity() {

    lateinit var signInButton: Button;
    lateinit var registerButton: Button;

    private lateinit var auth: FirebaseAuth

    lateinit var emailTextView: TextView
    lateinit var passwordTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        auth = Firebase.auth

        signInButton = findViewById(R.id.SignInButton)
        registerButton = findViewById(R.id.RegisterButton)

        emailTextView = findViewById(R.id.EmailTextView)
        passwordTextView = findViewById(R.id.PasswordTextView)

        signInButton.setOnClickListener {
            signIn(emailTextView.text.toString(), passwordTextView.text.toString())
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }

    public override fun onStart() {
        super.onStart()

//        val currentUser = auth.currentUser
//        if (currentUser != null) {
//            reload();
//        }
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
        if (user != null) {
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("userID",user.uid)
            startActivity(intent)
            finish()
        } else {
            reload()
        }
    }

    private fun reload() {
        finish()
        startActivity(getIntent())
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}