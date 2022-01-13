package ipca.project.rpglife

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import org.json.JSONException

import org.json.JSONTokener




class MainActivity : AppCompatActivity() {

    lateinit var user: User
    var userID: String? = null
    lateinit var textview : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textview = findViewById(R.id.textView)
        var db = Firebase.firestore
        userID = intent.getStringExtra("userID")
        val docRef = userID?.let { db.collection("users").document(it) }
        docRef?.get()
            ?.addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                    textview.setTextColor(Color.parseColor("#FF0000"));
                    user = parseUserJsonData(document.data.toString());
                    textview.text = user.Name
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            ?.addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.Profile -> {
                val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun parseUserJsonData(result : String) : User {
        try {
            val json = JSONTokener(result).nextValue() as JSONObject
            var NewUser: User = User(
                json["Name"].toString(),
                json["XP"].toString().toInt(),
                json["TotalSteps"].toString().toInt(),
                json["Calories"].toString().toFloat(),
                json["StartDate"].toString(),
                json["EndDate"].toString()
            )
            return NewUser
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return User("Name",0,0,0f,"00/00/0000","00/00/0000")
    }
}