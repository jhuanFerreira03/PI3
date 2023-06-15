package br.edu.puc.sorriso24h.views

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivityServiceBinding
import br.edu.puc.sorriso24h.infra.Constants
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage

class ServiceActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityServiceBinding

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var messaging : FirebaseMessaging
    private lateinit var storage : FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        messaging = FirebaseMessaging.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.btnVoltarRegister.setOnClickListener(this)
        binding.textView27.setOnClickListener(this)
        binding.textView29.setOnClickListener(this)
        binding.imageClose.setOnClickListener(this)

        setInfos()
    }
    @SuppressLint("SetTextI18n")
    private fun setInfos() {
        db.collection(Constants.DB.ATENDIMENTOS)
            .document(SecurityPreferences(this).getString(Constants.KEY_SHARED.ATENDIMENTO_ID).toString()).get()
            .addOnCompleteListener{ result ->
                val doc = result.result.data!! as HashMap
                binding.textNomeDetalhe.text = result.result.data!![Constants.DB.FIELD.NAME_DB].toString()
                binding.textTelefoneDetalhe.text = result.result.data!![Constants.DB.FIELD.PHONE].toString()
                if (doc.containsKey("estrelasProfissional") && doc["estrelasProfissional"] != ""){
                    binding.textNotaDent.text = doc["estrelasProfissional"].toString() + "/5"
                }
                if (doc.containsKey("notaApp") && doc["notaApp"] != ""){
                    binding.textNotaApp.text = doc["notaApp"].toString() + "/5"
                }
            }
    }
    private fun openPop(x : String) {
        db.collection(Constants.DB.ATENDIMENTOS).document(SecurityPreferences(this).getString(Constants.KEY_SHARED.ATENDIMENTO_ID).toString()).get().addOnCompleteListener {
            val doc = it.result.data as HashMap
            if (x == "comAtend") {
                if (doc.containsKey("comentarioAtendimento") && doc["comentarioAtendimento"] != "") {
                    binding.textComent.text = doc["comentarioAtendimento"].toString()
                }
            }
            else if (x == "comApp"){
                if (doc.containsKey("comentarioApp") && doc["comentarioApp"] != ""){
                    binding.textComent.text = doc["comentarioApp"].toString()
                }
            }
            binding.RelativeComents.visibility = View.VISIBLE
        }
    }
    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_voltar_register -> {
                startActivity(Intent(this, ServiceHistoricActivity::class.java))
                finish()
            }
            R.id.image_close -> {
                binding.RelativeComents.visibility = View.INVISIBLE
            }
            R.id.textView27 -> openPop("comAtend")
            R.id.textView29 -> openPop("comApp")
        }
    }
}