package br.edu.puc.sorriso24h

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import br.edu.puc.sorriso24h.databinding.ActivityRegisterBinding
import br.edu.puc.sorriso24h.infra.Constants
import br.edu.puc.sorriso24h.infra.SecurityPreferences

class RegisterActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var binding : ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.buttonRegister.setOnClickListener(this)

        binding.textNameEnd.setText(SecurityPreferences(this).getString(Constants.KEY.NAME_REGISTER))
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.button_register -> {
                Toast.makeText(this, Constants.PHRASE.NO_DB, Toast.LENGTH_LONG).show()
            }
        }
    }
}