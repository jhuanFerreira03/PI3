package br.edu.puc.sorriso24h.views

import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivityMapsBinding
import br.edu.puc.sorriso24h.databinding.ActivityServiceConfirmBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception
import java.util.Locale
import br.edu.puc.sorriso24h.infra.Constants
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint

class MapsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMapsBinding

    lateinit var place : Place

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var messaging : FirebaseMessaging

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        messaging = FirebaseMessaging.getInstance()

        onLat()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment
        mapFragment.getMapAsync {
            addPlace(it)
            it.setOnMapLoadedCallback {
                val bounds = LatLngBounds.builder()
                bounds.include(place.latLng)
                it.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 500))
            }
        }
    }
    private fun onLat() {
        try {
            place = Place("Localização socorrista",
                LatLng(
                    SecurityPreferences(this).getString("latitude")!!.toDouble(),
                    SecurityPreferences(this).getString("longitude")!!.toDouble()
                ),
                "Endereço Socorrista")
        } catch (_: Exception) {
        }
    }
    private fun addPlace(googleMap: GoogleMap) {
        googleMap.addMarker(
            MarkerOptions()
                .title(place.name)
                .snippet(place.address)
                .position(place.latLng)
        )
    }
}
data class Place(
    val name: String,
    val latLng: LatLng,
    val address: String
)