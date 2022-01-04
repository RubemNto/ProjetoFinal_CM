package ipca.project.rpglife

import android.content.Intent
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

    lateinit var signInButton: Button;
    lateinit var registerButton: Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        signInButton = findViewById(R.id.SignInButton)
        registerButton = findViewById(R.id.RegisterButton)

        signInButton.setOnClickListener {
            val intent = Intent(this,MapActivity::class.java)
            startActivity(intent)
        }

        registerButton.setOnClickListener {
            val intent = Intent(this,Register::class.java)
            startActivity(intent)
        }
    }

}