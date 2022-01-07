package ipca.project.rpglife

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {
    lateinit var knightOption: ImageView
    lateinit var vikingOption: ImageView
    lateinit var witchOption: ImageView

    lateinit var checkDotKnight: ImageView
    lateinit var checkDotViking: ImageView
    lateinit var checkDotWitch: ImageView

    lateinit var classDescription: TextView

    val classesDescriptions: ArrayList<String> = arrayListOf(
        "Health is magical",
        "Health brings honor",
        "Health makes the warrior"
    )

    lateinit var registerButton: Button

    private lateinit var auth: FirebaseAuth
    lateinit var EmailTextView: TextView
    lateinit var PasswordTextView: TextView
    lateinit var NameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth
        knightOption = findViewById(R.id.KnightImageView)
        vikingOption = findViewById(R.id.VikingImageView)
        witchOption = findViewById(R.id.WitchImageView)

        checkDotKnight = findViewById(R.id.SelectionKnightImageView)
        checkDotViking = findViewById(R.id.SelectionVikingImageView)
        checkDotWitch = findViewById(R.id.SelectionWitchImageView)

        classDescription = findViewById(R.id.DescriptionTextView)
        classDescription.text = classesDescriptions[1]

        knightOption.setOnClickListener {
            classDescription.text = classesDescriptions[1]
            checkDotKnight.visibility = View.VISIBLE
            checkDotViking.visibility = View.INVISIBLE
            checkDotWitch.visibility = View.INVISIBLE
        }

        vikingOption.setOnClickListener {
            classDescription.text = classesDescriptions[2]
            checkDotViking.visibility = View.VISIBLE
            checkDotKnight.visibility = View.INVISIBLE
            checkDotWitch.visibility = View.INVISIBLE
        }

        witchOption.setOnClickListener {
            classDescription.text = classesDescriptions[0]
            checkDotWitch.visibility = View.VISIBLE
            checkDotKnight.visibility = View.INVISIBLE
            checkDotViking.visibility = View.INVISIBLE
        }

        registerButton = findViewById(R.id.RegistrationRegisterButton)

        EmailTextView = findViewById(R.id.RegistrationEmailTextView)
        PasswordTextView = findViewById(R.id.RegistrationPasswordTextView)
        NameTextView = findViewById(R.id.RegistrationUsernameTextView)
        registerButton.setOnClickListener {
            createAccount(
                EmailTextView.text.toString(),
                PasswordTextView.text.toString(),
                NameTextView.text.toString()
            )
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//        if (currentUser != null) {
//            reload();
//        }
    }

    private fun reload() {
        finish()
        startActivity(getIntent())
    }

    private fun createAccount(email: String, password: String, name: String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val db = Firebase.firestore
                    val userData = hashMapOf(
                        "Name" to name,
                        "XP" to 0,
                        "Steps" to 0,
                        "Calories" to 0,
                    )
                    db.collection("users").document(user?.uid.toString()).set(userData)
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

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            reload()
        }
    }

    companion object {
        private const val TAG = "EmailPassword"
    }


}