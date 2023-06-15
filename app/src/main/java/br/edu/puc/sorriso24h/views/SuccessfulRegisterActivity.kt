package br.edu.puc.sorriso24h.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivitySuccessfulRegisterBinding

class SuccessfulRegisterActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySuccessfulRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySuccessfulRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.buttonBackToLogin.setOnClickListener(this)
    }
    override fun onClick(v: View) {
        when(v.id){
            R.id.button_backToLogin -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}