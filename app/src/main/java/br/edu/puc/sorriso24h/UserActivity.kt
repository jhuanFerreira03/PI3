package br.edu.puc.sorriso24h

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.edu.puc.sorriso24h.databinding.ActivityUserBinding
import br.edu.puc.sorriso24h.infra.Constants
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityUserBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        supportActionBar?.hide()

        binding.emailUsuario.text = SecurityPreferences(this).getString(Constants.KEY.EMAIL_LOGIN)
        binding.buttonLogout.setOnClickListener(this)
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
        }
    }
}