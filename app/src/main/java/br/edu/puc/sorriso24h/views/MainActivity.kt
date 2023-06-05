package br.edu.puc.sorriso24h.views

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivityMainBinding
import br.edu.puc.sorriso24h.infra.Constants
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestoreSettings

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth : FirebaseAuth

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        verifyUserlogin()

    }

    private fun autenUser(email:String, senha:String){
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener {auten ->
                if(auten.isSuccessful){
                    startActivity(Intent(this, UserActivity::class.java))
                    finish()
                }
            }.addOnFailureListener{
                startActivity(Intent(this, TelaLogin::class.java))
                finish()
            }
    }

    private fun verifyUserlogin(){
        if (SecurityPreferences(this).getString(Constants.KEY_SHARED.SAVE_LOGIN) != "") {
            if (SecurityPreferences(this).getString(Constants.KEY_SHARED.EMAIL_LOGIN) != "" &&
                SecurityPreferences(this).getString(Constants.KEY_SHARED.PASSWORD_LOGIN) != ""
            ) {
                autenUser(
                    SecurityPreferences(this).getString(Constants.KEY_SHARED.EMAIL_LOGIN).toString(),
                    SecurityPreferences(this).getString(Constants.KEY_SHARED.PASSWORD_LOGIN).toString())
            }
        } else {
            startActivity(Intent(this, TelaLogin::class.java))
            finish()
        }
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }
}