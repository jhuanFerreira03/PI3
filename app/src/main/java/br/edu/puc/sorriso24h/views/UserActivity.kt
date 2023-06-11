package br.edu.puc.sorriso24h.views

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColor
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivityUserBinding
import br.edu.puc.sorriso24h.infra.Constants
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class UserActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityUserBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var messaging : FirebaseMessaging


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        messaging = FirebaseMessaging.getInstance()

        supportActionBar?.hide()

        updateToken()
        verifyStatus()
        setName()

        binding.buttonLogout.setOnClickListener(this)
        binding.buttonEmergencyList.setOnClickListener(this)
        binding.buttonDetails.setOnClickListener(this)
        binding.buttonAtendimento.setOnClickListener(this)

        binding.switchButton.setOnClickListener{
            if(binding.switchButton.isChecked) updateStatus(true)
            else updateStatus(false)
        }
    }
    private fun setName(){
        db.collection(Constants.DB.DENTISTAS)
            .whereEqualTo("uid", auth.currentUser!!.uid)
            .get()
            .addOnCompleteListener {
                    doc ->
                binding.emailUsuario.setText(doc.result.documents[0].get("nome").toString())
            }
    }
    private fun updateStatus(status: Boolean) {
        val dados = hashMapOf(
            "status" to status
        )
        db.collection(Constants.DB.DENTISTAS)
            .whereEqualTo(Constants.DB.FIELD.UID, auth.currentUser!!.uid)
            .get()
            .addOnCompleteListener {
                val doc : DocumentSnapshot = it.result.documents[0]
                val docId : String = doc.id

                db.collection(Constants.DB.DENTISTAS)
                    .document(docId)
                    .update(dados as Map<String, Any>)
                    .addOnCompleteListener {}
            }
        if (status) {
            Snackbar.make(binding.buttonLogout, "Notificações ativadas!", Snackbar.LENGTH_LONG)
                .setBackgroundTint(Color.rgb(0,191,54))
                .show()
            SecurityPreferences(this).storeString(Constants.KEY_SHARED.NOTI, Constants.OTHERS.TRUE)
        }
        else {
            Snackbar.make(binding.buttonLogout, "Notificações desativadas!", Snackbar.LENGTH_LONG)
                .setBackgroundTint(Color.rgb(229,0,37))
                .show()
            SecurityPreferences(this).storeString(Constants.KEY_SHARED.NOTI, Constants.OTHERS.FALSE)
        }
    }
    private fun updateToken(){
        val token : String = messaging.token.result
        val uid : String = auth.uid.toString()

        db.collection(Constants.DB.DENTISTAS)
            .whereEqualTo("uid", uid)
            .get()
            .addOnCompleteListener {
                val doc : DocumentSnapshot = it.result.documents[0]
                val docId : String = doc.id

                db.collection(Constants.DB.DENTISTAS)
                    .document(docId)
                    .update("fcmToken", token)
                    .addOnCompleteListener{}
            }
    }
    private fun verifyStatus (){
        db.collection(Constants.DB.DENTISTAS)
            .whereEqualTo(Constants.DB.FIELD.EMAIL_DB, SecurityPreferences(this).getString(Constants.KEY_SHARED.EMAIL_LOGIN))
            .get()
            .addOnCompleteListener {
                val doc : DocumentSnapshot = it.result.documents[0]
                val docId : String = doc.id

                db.collection(Constants.DB.DENTISTAS)
                    .document(docId)
                    .addSnapshotListener{ doc, e ->
                        SecurityPreferences(this).storeString("sta", doc?.get("status").toString())
                    }
            }
        if(SecurityPreferences(this).getString("sta").toString() == Constants.OTHERS.FALSE.lowercase()) return
        else if (SecurityPreferences(this).getString("sta").toString() == Constants.OTHERS.TRUE.lowercase()) {
            binding.switchButton.isChecked = true
        }
    }
    private fun verifyService(){
        db.collection(Constants.DB.DENTISTAS).whereEqualTo(Constants.DB.FIELD.UID, auth.currentUser!!.uid).get().addOnCompleteListener {
            den ->
            db.collection("Atendimentos").whereEqualTo("status", "pendente")
                .get()
                .addOnCompleteListener {
                    var verivyPen = false
                    for (cop: DocumentSnapshot in it.result.documents) {
                        if (cop.get("dentista").toString() == den.result.documents[0].id) {
                            SecurityPreferences(this).storeString("emergencyNoti", cop.get("emergencia").toString())
                            verivyPen = true
                            break
                        }
                    }
                    if (verivyPen) {
                        startActivity(Intent(this, ServiceConfirmActivity::class.java))
                    } else {
                        Snackbar.make(binding.root, "Sem Atendimento Pendente!", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.RED)
                            .show()
                    }
                }
        }
    }
    override fun onClick(v: View) {
        when(v.id) {
            R.id.button_logout -> {
                updateStatus(false)
                auth.signOut()
                SecurityPreferences(this).storeString(Constants.KEY_SHARED.SAVE_LOGIN, "")
                SecurityPreferences(this).storeString(Constants.KEY_SHARED.EMAIL_LOGIN, "")
                SecurityPreferences(this).storeString(Constants.KEY_SHARED.PASSWORD_LOGIN, "")
                startActivity(Intent(this, TelaLogin::class.java))
                finish()
            }
            R.id.button_emergencyList -> {
                startActivity(Intent(this, EmergenciesActivity::class.java))
            }
            R.id.button_details -> {
                startActivity(Intent(this, AccountDetailsActivity::class.java))
            }
            R.id.button_atendimento -> {
                verifyService()
            }
        }
    }
}