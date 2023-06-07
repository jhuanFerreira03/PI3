package br.edu.puc.sorriso24h.views

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivityEmergencyDetailBinding
import br.edu.puc.sorriso24h.infra.Constants
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.messaging.FirebaseMessaging

class EmergencyDetailActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityEmergencyDetailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var messaging : FirebaseMessaging

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        messaging = FirebaseMessaging.getInstance()

        supportActionBar?.hide()

        binding.imageArrowBack.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))

        binding.textNomeDetalhe.text = SecurityPreferences(this).getString(Constants.KEY_SHARED.ARRAY_NAME)?.uppercase()
        binding.textTelefoneDetalhe.text = SecurityPreferences(this).getString(Constants.KEY_SHARED.ARRAY_TEL)?.uppercase()

        binding.btnVoltarRegister.setOnClickListener(this)
        binding.buttonAceitarEmergencia.setOnClickListener(this)
    }
    private fun aceitarEmergencia(){
        db.collection(Constants.DB.EMERGENCIAS)
            .whereEqualTo(Constants.DB.FIELD.PHONE, SecurityPreferences(this).getString(Constants.KEY_SHARED.ARRAY_TEL).toString())
            .get()
            .addOnCompleteListener{
                it ->
                val itId = it.result.documents[0].id
                db.collection(Constants.DB.DENTISTAS)
                    .whereEqualTo(Constants.DB.FIELD.EMAIL_DB, SecurityPreferences(this).getString(Constants.KEY_SHARED.EMAIL_LOGIN))
                    .get()
                    .addOnCompleteListener {
                    uid ->
                    val uidId = uid.result.documents[0].id
                    val cop : HashMap<String?, Any?> = it.result.documents[0].data as HashMap<String?, Any?>
                    var x = 1
                    if(!cop.containsValue(uidId)) {
                        for (count in cop.keys) {
                           if (count.toString() == "dentista_${x}") {
                               x++
                           }
                        }
                        db.collection(Constants.DB.EMERGENCIAS)
                            .document(itId)
                            .update("dentista_${x}", uidId)

                        Snackbar.make(binding.buttonAceitarEmergencia, Constants.PHRASE.EMERGENCY_ACCEPTED, Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.GREEN)
                            .show()
                        return@addOnCompleteListener

                    }
                    Snackbar.make(binding.buttonAceitarEmergencia, Constants.PHRASE.ALREADY_ACCEPTED_EMERGENCY, Snackbar.LENGTH_LONG)
                        .setBackgroundTint(Color.RED)
                        .show()
                }
            }
    }
    override fun onClick(v: View) {
        when (v.id){
            R.id.btn_voltar_register -> {
                startActivity(Intent(this, EmergenciesActivity::class.java))
                finish()
            }
            R.id.button_aceitar_emergencia -> {
                aceitarEmergencia()
            }
        }
    }
}
