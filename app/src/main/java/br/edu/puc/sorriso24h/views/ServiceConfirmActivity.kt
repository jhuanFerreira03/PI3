package br.edu.puc.sorriso24h.views

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivityServiceConfirmBinding
import br.edu.puc.sorriso24h.infra.Constants
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception
import java.util.Locale

class ServiceConfirmActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityServiceConfirmBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var messaging : FirebaseMessaging
    private val REQUEST_PHONE_CALL = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        messaging = FirebaseMessaging.getInstance()

        setInfo()

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL)
        }

        binding.btnVoltarRegister.setOnClickListener(this)
        binding.imageCall.setOnClickListener(this)
        binding.buttonSendLocation.setOnClickListener(this)
        binding.buttonGetLocation.setOnClickListener(this)
        binding.imageBtnSendLocation.setOnClickListener(this)
        binding.imageBtnGetLocation.setOnClickListener(this)

        db.collection(Constants.DB.ATENDIMENTOS).document(SecurityPreferences(this).getString(Constants.KEY_SHARED.ATENDIMENTO_ID).toString()).addSnapshotListener{
            doc, e ->
            try {
                if (doc!!.data!!["statusPendente"] == true) {
                    val x = doc!!.data!!["geolocalizacaoEmergencia"] as GeoPoint
                    SecurityPreferences(this).storeString("latitude", x.latitude.toString())
                    SecurityPreferences(this).storeString("longitude", x.longitude.toString())
                }
            } catch (e: Exception) {
                Snackbar.make(binding.root, e.message.toString(), Snackbar.LENGTH_LONG).show()
            }
        }

    }
    private fun setInfo() {
        db.collection(Constants.DB.ATENDIMENTOS).document(SecurityPreferences(this).getString(Constants.KEY_SHARED.ATENDIMENTO_ID).toString()).addSnapshotListener {
            doc, e ->
            binding.textNome.text = doc!![Constants.DB.FIELD.NAME_DB].toString()
            binding.textTelefone.text = doc[Constants.DB.FIELD.PHONE].toString()
        }
    }
    private fun setStatus(){
        db.collection(Constants.DB.ATENDIMENTOS)
            .document(SecurityPreferences(this).getString(Constants.KEY_SHARED.ATENDIMENTO_ID).toString())
            .update("statusPendente", false)
    }
    @SuppressLint("MissingPermission")
    private fun startCall() {
        val callint = Intent(Intent.ACTION_CALL)

        callint.data = Uri.parse("tel:" + binding.textTelefone.text.toString())

        startActivity(callint)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PHONE_CALL) startCall()
    }
    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_voltar_register -> {
                startActivity(Intent(this, UserActivity::class.java))
                finish()
            }
            R.id.image_call -> {
                startCall()
            }
            R.id.button_getLocation, R.id.image_btnGetLocation-> {
                startActivity(Intent(this, MapsActivity::class.java))
                setStatus()
            }
            R.id.button_sendLocation, R.id.image_btnSendLocation -> {
                startActivity(Intent(this, SendLocationActivity::class.java))
                setStatus()
            }
        }
    }
}