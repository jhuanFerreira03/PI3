package br.edu.puc.tenstadoessecarai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.ActionProvider.VisibilityListener
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import br.edu.puc.tenstadoessecarai.databinding.ActivityTelaLoginBinding
import br.edu.puc.tenstadoessecarai.infra.Constants
import br.edu.puc.tenstadoessecarai.infra.SecurityPreferences
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder

class TelaLogin : AppCompatActivity(), View.OnClickListener {

    private val TAG = "SignUpFragment"
    private lateinit var auth: FirebaseAuth
    private lateinit var functions: FirebaseFunctions
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()

    private lateinit var binding:ActivityTelaLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTelaLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        verifyUser()

        supportActionBar?.hide()

        binding.loginButton.setOnClickListener(this)
        binding.registerText.setOnClickListener(this)
    }

    private fun userLogin() {
        if (binding.email.text.toString().trim().isEmpty()) {
            binding.email.error = "Campo obrigatório"
            return
        }
        //Toast.makeText(this, "--${binding.email.text.trim()}--", Toast.LENGTH_SHORT).show()
        if (binding.password.text.toString().trim().isEmpty()) {
            binding.password.error = "Campo obrigatório"
            return
        }
        binding.progressLogin.visibility = View.VISIBLE
        //Thread.sleep(3000)

        if(binding.checkManter.isChecked){
            SecurityPreferences(this).storeString(Constants.KEY.Save, Constants.KEY.Saved)
        }
        SecurityPreferences(this).storeString(Constants.KEY.Email, binding.email.text.toString().trim())
        SecurityPreferences(this).storeString(Constants.KEY.Password, binding.email.text.toString().trim())
        // Para fins de exemplo, vamos apenas abrir a tela de cadastro quando clicar no botão Entrar
        startActivity(Intent(this, UserActivity::class.java))
        finish()
    }
    private fun updateUserProfile(nome: String, telefone: String, email: String, uid: String, fcmToken: String) : Task<CustomResponse> {
        // chamar a function para atualizar o perfil.
        functions = Firebase.functions("southamerica-east1")

        // Create the arguments to the callable function.
        val data = hashMapOf(
            "nome" to nome,
            "telefone" to telefone,
            "email" to email,
            "uid" to uid,
            "fcmToken" to fcmToken
        )

        return functions
            .getHttpsCallable("setUserProfile")
            .call(data)
            .continueWith { task ->

                val result = gson.fromJson((task.result?.data as String), CustomResponse::class.java)
                result
            }

    }

    fun verifyUser(){
        if (SecurityPreferences(this).getString(Constants.KEY.Save) != "") {
            if (SecurityPreferences(this).getString(Constants.KEY.Email) != "" &&
                SecurityPreferences(this).getString(Constants.KEY.Password) != ""
            ) {
                startActivity(Intent(this, UserActivity::class.java))
                finish()
            }
        }
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.login_button -> userLogin()
            R.id.register_text -> {
                startActivity(Intent(this, MilagreActivity::class.java))
                finish()
            }
        }
    }
}
