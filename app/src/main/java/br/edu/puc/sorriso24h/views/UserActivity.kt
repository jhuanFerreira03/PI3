package br.edu.puc.sorriso24h.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
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

        binding.emailUsuario.text = SecurityPreferences(this).getString(Constants.KEY_SHARED.EMAIL_LOGIN)
        binding.buttonLogout.setOnClickListener(this)
        binding.buttonEmergencyList.setOnClickListener(this)

        binding.switchButton.setOnClickListener{
            if(binding.switchButton.isChecked) updateStatus(true)
            else updateStatus(false)
        }
    }
    private fun updateStatus(status: Boolean) {
        val dados = hashMapOf(
            "status" to status
        )
        db.collection(Constants.DB.DENTISTAS).whereEqualTo(Constants.DB.FIELD.EMAIL_DB, SecurityPreferences(this).getString(Constants.KEY_SHARED.EMAIL_LOGIN))
            .get().addOnCompleteListener {
                val doc : DocumentSnapshot = it.result.documents[0]
                val docId : String = doc.id
                db.collection(Constants.DB.DENTISTAS).document(docId).update(dados as Map<String, Any>).addOnCompleteListener {}
            }
        if (status) {
            Snackbar.make(binding.buttonLogout, "Notificações ativadas!", Snackbar.LENGTH_LONG).show()
            SecurityPreferences(this).storeString(Constants.OTHERS.NOTI, Constants.OTHERS.TRUE)
        }
        else {
            Snackbar.make(binding.buttonLogout, "Notificações desativadas!", Snackbar.LENGTH_LONG).show()
            SecurityPreferences(this).storeString(Constants.OTHERS.NOTI, Constants.OTHERS.FALSE)
        }
    }
    private fun updateToken(){
        val token : String = messaging.token.result
        val uid : String = auth.uid.toString()

        db.collection(Constants.DB.DENTISTAS).whereEqualTo("uid", uid).get().addOnCompleteListener {
            val doc : DocumentSnapshot = it.result.documents[0]
            val docId : String = doc.id
            db.collection(Constants.DB.DENTISTAS).document(docId).update("fcmToken", token).addOnCompleteListener {}
        }
    }
    private fun verifyStatus (){
        db.collection(Constants.DB.DENTISTAS)
                .whereEqualTo(Constants.DB.FIELD.EMAIL_DB, SecurityPreferences(this).getString(Constants.KEY_SHARED.EMAIL_LOGIN)).
                get().addOnCompleteListener {
                    val doc : DocumentSnapshot = it.result.documents[0]
                    val docId : String = doc.id
                    db.collection(Constants.DB.DENTISTAS).document(docId).addSnapshotListener{ doc, e ->
                        SecurityPreferences(this).storeString("sta", doc?.get("status").toString())
                    }
                }
        if(SecurityPreferences(this).getString("sta").toString() == Constants.OTHERS.FALSE.lowercase()) return
        else if (SecurityPreferences(this).getString("sta").toString() == Constants.OTHERS.TRUE.lowercase()) {
            binding.switchButton.isChecked = true
        }
    }
    override fun onClick(v: View) {
        when(v.id) {
            R.id.button_logout -> {
                SecurityPreferences(this).storeString(Constants.KEY_SHARED.SAVE_LOGIN, "")
                SecurityPreferences(this).storeString(Constants.KEY_SHARED.EMAIL_LOGIN, "")
                SecurityPreferences(this).storeString(Constants.KEY_SHARED.PASSWORD_LOGIN, "")
                auth.signOut()
                startActivity(Intent(this, TelaLogin::class.java))
                finish()
            }
            R.id.button_emergencyList -> {
                startActivity(Intent(this, EmergenciesActivity::class.java))
            }
        }
    }
}