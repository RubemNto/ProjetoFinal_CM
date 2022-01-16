package ipca.project.rpglife

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ipca.project.rpglife.databinding.ActivityMapsBinding
import android.content.Context;
import android.content.pm.PackageManager
import org.json.JSONObject
import org.json.JSONException

import org.json.JSONTokener
import java.text.SimpleDateFormat
import java.util.*
import android.location.LocationListener

import android.location.LocationManager
import android.view.View
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    lateinit var user: User
    var userID: String? = null
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    protected var locationManager: LocationManager? = null
    protected var locationListener: LocationListener? = null
    private  var lat: Double = 0.0
    private  var lon: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000,10f,this)

        var db = Firebase.firestore
        userID = intent.getStringExtra("userID")
        val docRef = userID?.let { db.collection("users").document(it) }
        docRef?.get()
            ?.addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                    user = parseUserJsonData(document.data.toString());
                    val c: Calendar = Calendar.getInstance()
                    val sdf = SimpleDateFormat("dd-MMM-yyyy")
                    val userData = hashMapOf(
                        "Name" to user.Name,
                        "XP" to user.XP,
                        "Calories" to user.Calories,
                        "TotalSteps" to user.TotalSteps,
                        "StartDate" to user.StartDate,
                        "EndDate" to sdf.format(c.time),
                    )
                    //update information into the user document in the firebase database
                    userID?.let { id -> db.collection("users").document(id).set(userData) }
                    //change the users current date of usage to the next time the app is successfully opened
                    user.EndDate = sdf.format(c.time)
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            ?.addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val position = LatLng(lat, lon)
        mMap.addMarker(MarkerOptions().position(position).title("User's current position"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,15f))
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
                intent.putExtra("username",user.Name)
                intent.putExtra("steps",user.TotalSteps.toString())
                intent.putExtra("calories",user.Calories.toString())
                intent.putExtra("startDate",user.StartDate)
                intent.putExtra("endDate",user.EndDate)
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

    override fun onLocationChanged(p0: Location) {
        lat = p0.latitude
        lon = p0.longitude
    }

    override fun onProviderDisabled(provider: String) {
        Log.d("Latitude", "disable")
    }

    override fun onProviderEnabled(provider: String) {
        Log.d("Latitude", "enable")
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.d("Latitude", "status")
    }
}