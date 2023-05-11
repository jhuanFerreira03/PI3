package br.edu.puc.tenstadoessecarai

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.view.View
import br.edu.puc.tenstadoessecarai.databinding.ActivityUserBinding
import br.edu.puc.tenstadoessecarai.infra.Constants
import br.edu.puc.tenstadoessecarai.infra.SecurityPreferences

class UserActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                startActivity(Intent(this, TelaLogin::class.java))
                finish()
            }
        }
    }
}