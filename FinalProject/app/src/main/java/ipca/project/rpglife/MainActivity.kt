package ipca.project.rpglife

import android.Manifest
import android.R.attr
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import org.json.JSONException

import org.json.JSONTokener
import java.text.SimpleDateFormat
import java.util.*
import android.content.Context
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {

    var handler: Handler = Handler()
    var runnable: Runnable? = null

    var delay = 10000
    var createAnimal = true
    var animals = arrayListOf<ImageView>()
    var animalsDrawables = arrayListOf<Drawable>()

    //user variables
    lateinit var user: User
    var userID: String? = null
    lateinit var db: FirebaseFirestore

    //user step counter variables
    var sensorManager: SensorManager? = null
    var running = false
    var totalSteps = 0f
    var previousTotalSteps = 0f
    lateinit var WalkValueTextView: TextView
    lateinit var CaloriesTextView: TextView

    lateinit var XPTextView:TextView
    lateinit var CurrentLevelTextView:TextView
    lateinit var FutureLevelTextView:TextView

    //google maps variables
    private var map: GoogleMap? = null
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val defaultLocation = LatLng(41.1579, -8.6291)
    private var locationPermissionGranted = false
    private var lastKnownLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        animals.add(findViewById(R.id.bearImageView))
        animalsDrawables.add(animals[0].drawable)
        animals.add(findViewById(R.id.deerImageView))
        animalsDrawables.add(animals[1].drawable)
        animals.add(findViewById(R.id.wolfImageView))
        animalsDrawables.add(animals[2].drawable)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        WalkValueTextView = findViewById(R.id.WalkValueTextView)
        CaloriesTextView = findViewById(R.id.CaloriesTextView)

        XPTextView = findViewById(R.id.XPTextView)
        CurrentLevelTextView = findViewById(R.id.CurrentLevelTextView)
        FutureLevelTextView = findViewById(R.id.FutureLevelTextView)

        loadData()
        WalkValueTextView.text = 0.toString()
        CaloriesTextView.text = 0.toString()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map2) as SupportMapFragment
        mapFragment.getMapAsync(this)

        db = Firebase.firestore
        userID = intent.getStringExtra("userID")
        val docRef = userID?.let { db.collection("users").document(it) }
        docRef?.get()
            ?.addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                    user = parseUserJsonData(document.data.toString())

                    CurrentLevelTextView.text = (user.XP/100).toString()
                    FutureLevelTextView.text = (user.XP/100 + 1).toString()
                    XPTextView.text = (user.XP - CurrentLevelTextView.text.toString().toInt()*100).toString() + "%"
                    val c: Calendar = Calendar.getInstance()
                    val sdf = SimpleDateFormat("dd-MMM-yyyy")
                    val userData = hashMapOf(
                        "UserClass" to user.UserClass,
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

    override fun onResume() {
        handler.postDelayed(Runnable {
            runnable?.let { handler.postDelayed(it, delay.toLong()) }
            if (map != null) {
                updateLocationUI()
                getDeviceLocation()
            }
            if (createAnimal) {
                var randomPosition = Random().nextInt(3)
                var randomAnimal = Random().nextInt(3)
                animals[randomPosition].visibility = View.VISIBLE
                animals[randomPosition].isClickable = true
                animals[randomPosition].setOnClickListener {
                    user?.XP += 5
                    CurrentLevelTextView.text = (user.XP/100).toString()
                    FutureLevelTextView.text = (user.XP/100 + 1).toString()
                    XPTextView.text = (user.XP - CurrentLevelTextView.text.toString().toInt()*100).toString() + "%"
                    val docRef = userID?.let { db.collection("users").document(it) }
                    docRef?.get()
                        ?.addOnSuccessListener { document ->
                            if (document != null) {
                                Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                                val c: Calendar = Calendar.getInstance()
                                val sdf = SimpleDateFormat("dd-MMM-yyyy")
                                val userData = hashMapOf(
                                    "UserClass" to user.UserClass,
                                    "Name" to user.Name,
                                    "XP" to user.XP,
                                    "Calories" to user.Calories,
                                    "TotalSteps" to user.TotalSteps,
                                    "StartDate" to user.StartDate,
                                    "EndDate" to sdf.format(c.time),
                                )
                                //update information into the user document in the firebase database
                                userID?.let { id ->
                                    db.collection("users").document(id).set(userData)
                                }
                                //change the users current date of usage to the next time the app is successfully opened
                                user.EndDate = sdf.format(c.time)
                            } else {
                                Log.d("TAG", "No such document")
                            }
                        }
                        ?.addOnFailureListener { exception ->
                            Log.d("TAG", "get failed with ", exception)
                        }
                    createAnimal = true
                    animals[randomPosition].visibility = View.GONE
                }
                animals[randomPosition].setImageDrawable(animalsDrawables[randomAnimal])
                createAnimal = false
            }
        }.also { runnable = it }, delay.toLong())
        super.onResume()
        running = true
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            Toast.makeText(this, "No step counter detected", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onPause() {
        super.onPause()
        runnable?.let { handler.removeCallbacks(it) } //stop handler when activity not visible super.onPause();
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (running) {
            totalSteps = event!!.values[0]
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            user.XP+=currentSteps
            WalkValueTextView.text = currentSteps.toInt().toString()
            CaloriesTextView.text = (currentSteps * 0.1f).toString()
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    private fun saveData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)
        Log.d("MainActivity", "$savedNumber")
        previousTotalSteps = savedNumber
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
        map?.uiSettings?.isZoomControlsEnabled = false
        map?.uiSettings?.isZoomGesturesEnabled = false
        map!!.setMinZoomPreference(DEFAULT_ZOOM.toFloat())
        map!!.setMaxZoomPreference(DEFAULT_ZOOM.toFloat())

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()
        // Get the current location of the device and set the position of the map.
        getDeviceLocation()
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
                intent.putExtra("userID",userID)
                intent.putExtra("userClass", user.UserClass.toString())
                intent.putExtra("username", user.Name)
                intent.putExtra("steps", (user.TotalSteps + WalkValueTextView.text.toString().toInt()).toString())
                intent.putExtra("calories", (user.Calories + CaloriesTextView.text.toString().toInt()).toString())
                intent.putExtra("startDate", user.StartDate)
                intent.putExtra("endDate", user.EndDate)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun parseUserJsonData(result: String): User {
        try {
            val json = JSONTokener(result).nextValue() as JSONObject
            return User(
                json["UserClass"].toString().toInt(),
                json["Name"].toString(),
                json["XP"].toString().toInt(),
                json["TotalSteps"].toString().toInt(),
                json["Calories"].toString().toFloat(),
                json["StartDate"].toString(),
                json["EndDate"].toString()
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return User(0, "Name", 0, 0, 0f, "00/00/0000", "00/00/0000")
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                            updateLocationUI()
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        map?.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = false
                map?.uiSettings?.isRotateGesturesEnabled = false
                map?.uiSettings?.isScrollGesturesEnabled = false
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    companion object {
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private val TAG = "Something"

        // Keys for storing activity state.
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
    }
}