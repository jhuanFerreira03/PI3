package br.edu.puc.sorriso24h

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import br.edu.puc.sorriso24h.databinding.ActivityRegisterBinding
import br.edu.puc.sorriso24h.infra.Constants
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.GsonBuilder
import org.json.JSONObject

class RegisterActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()
    private lateinit var functions: FirebaseFunctions
    private val TAG = "SignUpFragment"

    private lateinit var binding : ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        auth = FirebaseAuth.getInstance()
        functions = FirebaseFunctions.getInstance()
        db = FirebaseFirestore.getInstance()

        supportActionBar?.hide()

        binding.buttonRegister.setOnClickListener(this)

        binding.textNameEnd.setText(SecurityPreferences(this).getString(Constants.KEY.NAME_REGISTER))
    }

    private fun signUpNewAccount(nome: String, telefone: String, email: String, senha: String) {
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user!!.uid

                    // Enviar dados de usuário para o Cloud Functions
                    val dados = hashMapOf(
                        "uid" to uid,
                        "nome" to nome,
                        "telefone" to telefone,
                        "email" to email
                    )

                    functions.getHttpsCallable("setUserAccount")
                        .call(dados)
                        .continueWith { task ->
                            val result = task.result?.data as String?
                            val json = JSONObject(result)
                            val status = json.getString("status")
                            val message = json.getString("message")

                            // Verificar se houve algum erro ao criar a conta
                            if (status == "error") {
                                Snackbar.make(
                                    binding.buttonRegister,
                                    message,
                                    Snackbar.LENGTH_LONG
                                ).show()
                                false
                            }
                        }
                } else {
                    Log.e("milagre_activity", "${task.exception}")
                    // Exibir mensagem de erro ao criar a conta
                    Log.e(TAG, task.exception?.message ?: "Erro ao criar a conta")
                    Snackbar.make(binding.buttonRegister, "Erro ao criar a conta", Snackbar.LENGTH_LONG).show()
                }
            }
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.button_register -> {
                signUpNewAccount(SecurityPreferences(this).getString(Constants.KEY.NAME_REGISTER).toString(),
                    SecurityPreferences(this).getString(Constants.KEY.PHONE_NUMBER_REGISTER).toString(),
                    SecurityPreferences(this).getString(Constants.KEY.EMAIL_REGISTER).toString(),
                    SecurityPreferences(this).getString(Constants.KEY.PASSWORD_REGISTER).toString())
                //Toast.makeText(this, Constants.PHRASE.NO_DB, Toast.LENGTH_LONG).show()
            }
        }
    }
}