package br.edu.puc.sorriso24h.views

import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivitySendLocationBinding
import br.edu.puc.sorriso24h.databinding.ActivityServiceConfirmBinding
import br.edu.puc.sorriso24h.infra.Constants
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.messaging.FirebaseMessaging
import java.util.Locale

class SendLocationActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivitySendLocationBinding

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var messaging : FirebaseMessaging

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        messaging = FirebaseMessaging.getInstance()

        supportActionBar?.hide()

        binding.btnVoltarRegister.setOnClickListener(this)
        binding.button1.setOnClickListener(this)
        binding.button2.setOnClickListener(this)
        binding.button3.setOnClickListener(this)
    }
    private fun setInfo(){
        db.collection(Constants.DB.DENTISTAS).whereEqualTo(Constants.DB.FIELD.UID, auth.currentUser!!.uid).get().addOnCompleteListener {
            val x = it.result.documents[0].data as HashMap
            if (x.containsKey("endereco") && x["endereco"] != ""){
                binding.button1.text = x["endereco"].toString()
            }
            if (x.containsKey("endereco_2") && x["endereco_2"] != ""){
                binding.button1.text = x["endereco_2"].toString()
            }
            if (x.containsKey("endereco_3") && x["endereco_3"] != ""){
                binding.button1.text = x["endereco_3"].toString()
            }
        }
    }
    private fun onLat(x:String) : GeoPoint {
        val geo = Geocoder(this, Locale.getDefault())
        try {
            val list: MutableList<Address> = geo.getFromLocationName(x, 1) as MutableList<Address>
            if (list.size > 0) {
                val address : Address = list[0]
                return GeoPoint(address.latitude, address.longitude)
            }
        }
        catch (_:Exception) {
            Snackbar.make(binding.root, "Falha ao enviar o Endereço!", Snackbar.LENGTH_LONG)
                .setBackgroundTint(Color.rgb(229,0,37))
                .show()
        }
        return GeoPoint(0.0, 0.0)
    }
    private fun sendLocation(end : String) {
        db.collection(Constants.DB.DENTISTAS)
            .whereEqualTo(Constants.DB.FIELD.UID, auth.currentUser!!.uid).get()
            .addOnCompleteListener {dent ->
                val addlist = dent.result.documents[0].data!![end].toString().split(",")
                val add = "${addlist[1]}, ${addlist[2]}, ${addlist[3]}, ${addlist[4]}, ${addlist[5]}, ${addlist[6]}"
                db.collection(Constants.DB.ATENDIMENTOS)
                    .document(SecurityPreferences(this).getString(Constants.KEY_SHARED.ATENDIMENTO_ID).toString())
                    .update("geolocalizacaoDentista", onLat(add))
                Snackbar.make(binding.root, "Localização enviada!", Snackbar.LENGTH_LONG).setBackgroundTint(Color.rgb(0,191,54)).show()
            }
    }
    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_voltar_register -> {
                startActivity(Intent(this, ServiceConfirmActivity::class.java))
                finish()
            }
            R.id.button1 -> sendLocation("endereco")
            R.id.button2 -> sendLocation("endereco_2")
            R.id.button3 -> sendLocation("endereco_3")
        }
    }
}