package br.edu.puc.sorriso24h.views

import android.content.Intent
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

        binding.textNomeDetalhe.setText(SecurityPreferences(this).getString(Constants.KEY.ARRAY_NAME)?.uppercase())
        binding.textTelefoneDetalhe.setText(SecurityPreferences(this).getString(Constants.KEY.ARRAY_TEL)?.uppercase())

        binding.btnVoltarRegister.setOnClickListener(this)
        binding.buttonAceitarEmergencia.setOnClickListener(this)
    }
    private fun aceitarEmergencia(){
        db.collection("DadosSocorristas").whereEqualTo("nome", SecurityPreferences(this).getString(Constants.KEY.ARRAY_NAME).toString())
            .get().addOnCompleteListener {
                val doc : DocumentSnapshot = it.getResult().documents.get(0)
                val docId : String = doc.id
                val dad : HashMap<String?, Any?> = doc.getData() as HashMap<String?, Any?>
                var x  = 0
                do {
                    x++
                    val d : Task<QuerySnapshot> = db.collection("Users")
                        .whereEqualTo("email", SecurityPreferences(this).getString(Constants.KEY.EMAIL_LOGIN).toString())
                        .get().addOnCompleteListener {}
                    if(!dad.containsKey("dentista_${x}")){
                        db.collection("DadosSocorristas").document(docId).update("dentista_${x}", it.result.documents.get(0).id)
                        Snackbar.make(binding.buttonAceitarEmergencia, "Voce aceitou essa Emergencia, Aguarde o retorno!", Snackbar.LENGTH_LONG).show()
                        return@addOnCompleteListener
                    }
                    else{
                        if (dad.get("dentista_${x}") == it.result.documents.get(0).id){
                            Snackbar.make(binding.buttonAceitarEmergencia, "Voce ja aceitou essa emergencia, Aguarde o retorno!", Snackbar.LENGTH_LONG).show()
                            return@addOnCompleteListener
                        }
                    }
                }while (true)
            }
    }
    override fun onClick(v: View) {
        when (v.id){
            R.id.btn_voltar_register -> {
                startActivity(Intent(this, EmergencyActivity::class.java))
                finish()
            }
            R.id.button_aceitar_emergencia -> {
                aceitarEmergencia()
            }
        }
    }
}
/*db.collection("Users")
.whereEqualTo("email", SecurityPreferences(this)
.getString(Constants.KEY.EMAIL_LOGIN).toString()).get().result.documents.get(0).id)*/
