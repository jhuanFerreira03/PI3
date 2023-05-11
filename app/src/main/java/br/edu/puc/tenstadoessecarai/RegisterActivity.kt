package br.edu.puc.tenstadoessecarai

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import br.edu.puc.tenstadoessecarai.databinding.ActivityRegisterBinding
import br.edu.puc.tenstadoessecarai.infra.Constants
import br.edu.puc.tenstadoessecarai.infra.SecurityPreferences

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
                Toast.makeText(this, "Sem banco de dados(por enquanto) irmão! ;-;", Toast.LENGTH_LONG).show()
            }
        }
    }
}