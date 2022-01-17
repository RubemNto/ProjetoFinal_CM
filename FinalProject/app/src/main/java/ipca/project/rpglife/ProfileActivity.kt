package ipca.project.rpglife

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

class ProfileActivity : AppCompatActivity() {

    lateinit var ProfilePictureImageView: ImageView
    lateinit var UsernameTextView: TextView
    lateinit var StepsTextView: TextView
    lateinit var CaloriesTextView: TextView

    lateinit var StartDateTextView: TextView
    lateinit var EndDateTextView: TextView

    lateinit var ResetButton:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        ProfilePictureImageView = findViewById(R.id.ProfilePictureImageView)

        when (intent.getStringExtra("userClass")) {
            "0" -> {
        ProfilePictureImageView.setImageResource(R.drawable.rogue)
            }
            "1" -> {
        ProfilePictureImageView.setImageResource(R.drawable.barbarian)
            }
            else -> {
        ProfilePictureImageView.setImageResource(R.drawable.warrior)
            }
        }

        UsernameTextView = findViewById(R.id.UsernameTextView)
        UsernameTextView.text = intent.getStringExtra("username")
        StepsTextView = findViewById(R.id.StepsValueTextView)
        StepsTextView.text = intent.getStringExtra("steps")
        CaloriesTextView = findViewById(R.id.CaloriesValueTextView)
        CaloriesTextView.text = intent.getStringExtra("calories")

        StartDateTextView = findViewById(R.id.StartDateTextView)
        StartDateTextView.text = "from\n" + intent.getStringExtra("startDate")
        EndDateTextView = findViewById(R.id.EndDateTextView)
        EndDateTextView.text = "to\n" + intent.getStringExtra("endDate")

        ResetButton = findViewById(R.id.ResetProgressButton)
        ResetButton.isClickable = true

        ResetButton.setOnClickListener {
            ResetUser(intent.getStringExtra("userID"))
            finish()
            exitProcess(0)
        }
    }

    private fun ResetUser(userID: String?) {
        if(userID != null){
            val db = Firebase.firestore
            val docRef = userID?.let { db.collection("users").document(it) }
            docRef?.get()
                ?.addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                        val c: Calendar = Calendar.getInstance()
                        val sdf = SimpleDateFormat("dd-MMM-yyyy")
                        val userData = hashMapOf(
                            "UserClass" to (intent.getStringExtra("userClass")?.toInt()),
                            "Name" to intent.getStringExtra("username"),
                            "XP" to 0,
                            "Calories" to 0,
                            "TotalSteps" to 0,
                            "StartDate" to sdf.format(c.time),
                            "EndDate" to sdf.format(c.time),
                        )
                        //update information into the user document in the firebase database
                        userID?.let { id -> db.collection("users").document(id).set(userData) }
                        //change the users current date of usage to the next time the app is successfully opened
                    } else {
                        Log.d("TAG", "No such document")
                    }
                }
                ?.addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)
                }
        }
    }
}