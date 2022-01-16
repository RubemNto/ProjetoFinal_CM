package ipca.project.rpglife

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ProfileActivity : AppCompatActivity() {

    lateinit var UsernameTextView: TextView
    lateinit var StepsTextView: TextView
    lateinit var CaloriesTextView: TextView

    lateinit var StartDateTextView : TextView
    lateinit var EndDateTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
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
    }
}