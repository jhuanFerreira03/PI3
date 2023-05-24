package br.edu.puc.sorriso24h

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import br.edu.puc.sorriso24h.databinding.ActivityEmergencyBinding

class EmergencyActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityEmergencyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.imageArrowBack.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
        binding.btnVoltarRegister.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_voltar_register -> {
                startActivity(Intent(this, UserActivity::class.java))
                finish()
            }
        }
    }
}