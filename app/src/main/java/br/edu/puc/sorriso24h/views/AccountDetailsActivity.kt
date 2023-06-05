package br.edu.puc.sorriso24h.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivityAccountDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import br.edu.puc.sorriso24h.infra.Constants

class AccountDetailsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityAccountDetailsBinding

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var messaging : FirebaseMessaging

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        messaging = FirebaseMessaging.getInstance()

        binding.imageArrowBack.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))

        binding.btnVoltarRegister.setOnClickListener(this)

        setInfos()
    }

    private fun setInfos(){
        db.collection(Constants.DB.DENTISTAS)
            .whereEqualTo("uid", auth.currentUser!!.uid)
            .get()
            .addOnCompleteListener {
                doc ->
                binding.textAccountDetailNome.setText(doc.result.documents[0].get(Constants.DB.FIELD.NAME_DB).toString())
                binding.textAccountDetailEmail.setText(doc.result.documents[0].get(Constants.DB.FIELD.EMAIL_DB).toString())
                binding.textAccountDetailTelefone.setText(doc.result.documents[0].get(Constants.DB.FIELD.PHONE).toString())
            }
    }
    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_voltar_register -> {
                startActivity(Intent(this, UserActivity::class.java))
                finish()
            }
        }
    }
}