package br.edu.puc.sorriso24h

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.edu.puc.sorriso24h.databinding.ActivityTelaLoginBinding
import br.edu.puc.sorriso24h.infra.Constants
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.common.base.Verify
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import kotlin.math.log

class TelaLogin : AppCompatActivity(), View.OnClickListener {

    private val TAG = "SignUpFragment"
    private lateinit var auth: FirebaseAuth
    private lateinit var functions: FirebaseFunctions
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()

    private lateinit var binding:ActivityTelaLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        verifyUserlogado()

        binding = ActivityTelaLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.hide()

        binding.progressLogin.visibility = View.INVISIBLE

        binding.loginButton.setOnClickListener(this)
        binding.registerText.setOnClickListener(this)
    }

    private fun userLogin() {
        if (binding.email.text.toString().trim().isEmpty()) {
            binding.email.error = Constants.PHRASE.EMPTY_FIELD
            return
        }
        if (binding.password.text.toString().trim().isEmpty()) {
            binding.password.error = Constants.PHRASE.EMPTY_FIELD
            return
        }
        binding.progressLogin.visibility = View.VISIBLE

        autenUser(binding.email.text.toString().trim(), binding.password.text.toString().trim(), true)
    }

    private fun autenUser(email:String, senha:String, save:Boolean){
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener {auten ->
                if(auten.isSuccessful){
                    if(save) {
                        if (binding.checkManter.isChecked) {
                            SecurityPreferences(this).storeString(
                                Constants.KEY.SAVE_LOGIN,
                                Constants.KEY.SAVED_LOGIN
                            )
                            SecurityPreferences(this).storeString(Constants.KEY.NOTI, Constants.KEY.FALSE)
                        }
                        else {
                            SecurityPreferences(this).storeString(Constants.KEY.SAVE_LOGIN, "")
                            SecurityPreferences(this).storeString(Constants.KEY.NOTI, "")
                        }

                        SecurityPreferences(this).storeString(
                            Constants.KEY.EMAIL_LOGIN,
                            binding.email.text.toString().trim()
                        )
                        SecurityPreferences(this).storeString(
                            Constants.KEY.PASSWORD_LOGIN,
                            binding.password.text.toString().trim()
                        )
                    }

                    startActivity(Intent(this, UserActivity::class.java))
                    finish()
                }
            }.addOnFailureListener{
                    task ->
                Snackbar.make(binding.loginButton, "Não foi possivel autenticar o usuario!\n" + task.message, Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.RED).show()
                binding.progressLogin.visibility = View.INVISIBLE
            }
    }

    private fun verifyUserlogado(){

        if (SecurityPreferences(this).getString(Constants.KEY.SAVE_LOGIN) != "") {
            if (SecurityPreferences(this).getString(Constants.KEY.EMAIL_LOGIN) != "" &&
                SecurityPreferences(this).getString(Constants.KEY.PASSWORD_LOGIN) != ""
            ) {
                autenUser(SecurityPreferences(this).getString(Constants.KEY.EMAIL_LOGIN).toString(),
                    SecurityPreferences(this).getString(Constants.KEY.PASSWORD_LOGIN).toString(),
                    false)
            }
        }
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.login_button -> userLogin()
            R.id.register_text -> {
                startActivity(Intent(this, MilagreActivity::class.java))
                binding.email.setText("")
                binding.password.setText("")
            }
        }
    }
}
