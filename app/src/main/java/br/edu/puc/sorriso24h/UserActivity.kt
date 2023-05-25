package br.edu.puc.sorriso24h

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ScaleGestureDetector
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import br.edu.puc.sorriso24h.databinding.ActivityUserBinding
import br.edu.puc.sorriso24h.infra.Constants
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.awaitAll

class UserActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityUserBinding
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

        binding.emailUsuario.text = SecurityPreferences(this).getString(Constants.KEY.EMAIL_LOGIN)
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
        db.collection("Users").whereEqualTo("email", SecurityPreferences(this).getString(Constants.KEY.EMAIL_LOGIN))
            .get().addOnCompleteListener {
                val doc : DocumentSnapshot = it.getResult().documents.get(0)
                val docId : String = doc.id
                db.collection("Users").document(docId).update(dados as Map<String, Any>).addOnCompleteListener {}
            }
        if (status) {
            Snackbar.make(binding.buttonLogout, "Notificações ativadas!", Snackbar.LENGTH_LONG).show()
            SecurityPreferences(this).storeString(Constants.KEY.NOTI, Constants.KEY.TRUE)
        }
        else {
            Snackbar.make(binding.buttonLogout, "Notificações desativadas!", Snackbar.LENGTH_LONG).show()
            SecurityPreferences(this).storeString(Constants.KEY.NOTI, Constants.KEY.FALSE)
        }
    }
    private fun updateToken(){
        val token : String = messaging.token.result
        val uid : String = auth.uid.toString()

        db.collection("Users").whereEqualTo("uid", uid).get().addOnCompleteListener {
            val doc : DocumentSnapshot = it.getResult().documents.get(0)
            val docId : String = doc.id
            db.collection("Users").document(docId).update("fcmToken", token).addOnCompleteListener {}
        }
    }
    private fun verifyStatus (){
        db.collection("Users")
                .whereEqualTo("email", SecurityPreferences(this).getString(Constants.KEY.EMAIL_LOGIN)).
                get().addOnCompleteListener {
                    val doc : DocumentSnapshot = it.result.documents.get(0)
                    val docId : String = doc.id
                    db.collection("Users").document(docId).addSnapshotListener{ doc, e ->
                        SecurityPreferences(this).storeString("sta", doc?.get("status").toString())
                    }
                }
        if(SecurityPreferences(this).getString("sta").toString() == Constants.KEY.FALSE.lowercase()) return
        else if (SecurityPreferences(this).getString("sta").toString() == Constants.KEY.TRUE.lowercase()) {
            binding.switchButton.isChecked = true
        }
    }
    override fun onClick(v: View) {
        when(v.id) {
            R.id.button_logout -> {
                SecurityPreferences(this).storeString(Constants.KEY.SAVE_LOGIN, "")
                SecurityPreferences(this).storeString(Constants.KEY.EMAIL_LOGIN, "")
                SecurityPreferences(this).storeString(Constants.KEY.PASSWORD_LOGIN, "")
                auth.signOut()
                startActivity(Intent(this, TelaLogin::class.java))
                finish()
            }
            R.id.button_emergencyList -> {
                startActivity(Intent(this, EmergencyActivity::class.java))
            }
        }
    }
}