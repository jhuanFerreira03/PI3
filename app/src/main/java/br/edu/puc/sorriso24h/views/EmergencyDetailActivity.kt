package br.edu.puc.sorriso24h.views

import android.content.Intent
import android.graphics.BitmapFactory
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
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import kotlin.concurrent.thread

class EmergencyDetailActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityEmergencyDetailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var messaging : FirebaseMessaging
    private lateinit var storage : FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        messaging = FirebaseMessaging.getInstance()
        storage = FirebaseStorage.getInstance()

        supportActionBar?.hide()
        setInfos()

        binding.btnVoltarRegister.setOnClickListener(this)
        binding.buttonAceitarEmergencia.setOnClickListener(this)
        binding.imageDetail1.setOnClickListener(this)
        binding.imageDetail2.setOnClickListener(this)
        binding.imageDetail3.setOnClickListener(this)
    }
    private fun setInfos() {
        db.collection(Constants.DB.EMERGENCIAS)
            .document(SecurityPreferences(this).getString(Constants.KEY_SHARED.EMERGENCY_ID).toString())
            .get()
            .addOnCompleteListener {
            result ->
            binding.textNomeDetalhe.text = result.result[Constants.DB.FIELD.NAME_DB].toString()
            binding.textTelefoneDetalhe.text = result.result[Constants.DB.FIELD.PHONE].toString()
        }
        val file1 : File = File.createTempFile("tempfile", ".jpg")
        storage.getReference("images/${SecurityPreferences(this).getString(Constants.KEY_SHARED.EMERGENCY_ID).toString()}1")
            .getFile(file1)
            .addOnSuccessListener {
            binding.imageDetail1.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            binding.imageDetail1.setImageBitmap(BitmapFactory.decodeFile(file1.absolutePath))
        }
        val file2 : File = File.createTempFile("tempfile", ".jpg")
        storage.getReference("images/${SecurityPreferences(this).getString(Constants.KEY_SHARED.EMERGENCY_ID).toString()}2")
            .getFile(file2)
            .addOnSuccessListener {
            binding.imageDetail2.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            binding.imageDetail2.setImageBitmap(BitmapFactory.decodeFile(file2.absolutePath))
        }
        val file3 : File = File.createTempFile("tempfile", ".jpg")
        storage.getReference("images/${SecurityPreferences(this).getString(Constants.KEY_SHARED.EMERGENCY_ID).toString()}3")
            .getFile(file3)
            .addOnSuccessListener {
            binding.imageDetail3.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            binding.imageDetail3.setImageBitmap(BitmapFactory.decodeFile(file3.absolutePath))
        }
        Thread.sleep(500)
        binding.imageDetail1.visibility = View.VISIBLE
        binding.imageDetail2.visibility = View.VISIBLE
        binding.imageDetail3.visibility = View.VISIBLE
    }
    private fun aceitarEmergencia() {
        db.collection(Constants.DB.EMERGENCIAS)
            .document(SecurityPreferences(this).getString(Constants.KEY_SHARED.EMERGENCY_ID).toString()).get().addOnCompleteListener{
                it->
                db.collection(Constants.DB.DENTISTAS)
                    .whereEqualTo(Constants.DB.FIELD.UID, auth.currentUser!!.uid)
                    .get()
                    .addOnCompleteListener {
                    uid ->
                    val uidId = uid.result.documents[0].id
                    val cop : HashMap<String?, Any?> = it.result.data as HashMap<String?, Any?>
                    var x = 1
                    if(!cop.containsValue(uidId)) {
                        for (count in cop.keys) {
                           if (count.toString() == "${Constants.DB.FIELD.DENTISTA}_${x}") {
                               x++
                           }
                        }
                        db.collection(Constants.DB.EMERGENCIAS)
                            .document(it.result.id)
                            .update("${Constants.DB.FIELD.DENTISTA}_${x}", uidId)
                        Snackbar.make(binding.buttonAceitarEmergencia, Constants.PHRASE.EMERGENCY_ACCEPTED, Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.rgb(0,191,54))
                            .show()
                        return@addOnCompleteListener
                    }
                    Snackbar.make(binding.buttonAceitarEmergencia, Constants.PHRASE.ALREADY_ACCEPTED_EMERGENCY, Snackbar.LENGTH_LONG)
                        .setBackgroundTint(Color.rgb(229,0,37))
                        .show()
                }
            }
    }
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_voltar_register -> {
                startActivity(Intent(this, EmergenciesActivity::class.java))
                finish()
            }
            R.id.button_aceitar_emergencia -> {
                aceitarEmergencia()
            }
            R.id.image_detail_1 -> {
                SecurityPreferences(this).storeString(Constants.KEY_SHARED.PHOTO_VIEW_DECIDER, Constants.KEY_SHARED.EMERGENCY_DETAIL)
                SecurityPreferences(this).storeString(Constants.KEY_SHARED.PHOTO_VIEW,
                    "images/${SecurityPreferences(this).getString(Constants.KEY_SHARED.EMERGENCY_ID).toString()}1"
                )
                startActivity(Intent(this, PhotoViewActivity::class.java))
            }
            R.id.image_detail_2 -> {
                SecurityPreferences(this).storeString(Constants.KEY_SHARED.PHOTO_VIEW_DECIDER, Constants.KEY_SHARED.EMERGENCY_DETAIL)
                SecurityPreferences(this).storeString(
                    Constants.KEY_SHARED.PHOTO_VIEW,
                    "images/${SecurityPreferences(this).getString(Constants.KEY_SHARED.EMERGENCY_ID).toString()}2"
                )
                startActivity(Intent(this, PhotoViewActivity::class.java))
            }
            R.id.image_detail_3 -> {
                SecurityPreferences(this).storeString(Constants.KEY_SHARED.PHOTO_VIEW_DECIDER, Constants.KEY_SHARED.EMERGENCY_DETAIL)
                SecurityPreferences(this).storeString(Constants.KEY_SHARED.PHOTO_VIEW,
                    "images/${SecurityPreferences(this).getString(Constants.KEY_SHARED.EMERGENCY_ID).toString()}3"
                )
                startActivity(Intent(this, PhotoViewActivity::class.java))
            }
        }
    }
}
