package br.edu.puc.sorriso24h.views

import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception
import java.util.Locale

class MapsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMapsBinding

    lateinit var place : Place

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

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
    private fun onLat(){
        val x = "Av. Brg. Faria Lima, 3477 - 18º Andar - Itaim Bibi, São Paulo - SP, 04538-133"

        val geo = Geocoder(this, Locale.getDefault())

        try {
            val list: MutableList<Address> = geo.getFromLocationName(x, 1) as MutableList<Address>
            if (list.size > 0) {
                val address : Address = list[0]

                place = Place("sla", LatLng(address.latitude, address.longitude), "sla")
            }
        }
        catch (_:Exception){
        }
    }
    private fun addPlace(googleMap: GoogleMap){
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