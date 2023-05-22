package br.edu.puc.sorriso24h

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import br.edu.puc.sorriso24h.databinding.ActivityRegisterBinding
import br.edu.puc.sorriso24h.infra.Constants
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
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

        binding.imageArrowBack.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))

        auth = FirebaseAuth.getInstance()
        functions = FirebaseFunctions.getInstance()
        db = FirebaseFirestore.getInstance()

        supportActionBar?.hide()

        binding.buttonRegister.setOnClickListener(this)
        binding.btnVoltarRegister.setOnClickListener(this)
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

                    functions.getHttpsCallable("addUser").call(dados).continueWith{ task ->
                        val json = JSONObject(task.result?.data as String)
                        val status = json.getString("status")
                        val message = json.getString("message")
                        if (status.uppercase() == Constants.PHRASE.ERROR) {
                            Snackbar.make(
                                binding.buttonRegister, message, Snackbar.LENGTH_LONG).show()
                        }
                    }

                    /*functions.getHttpsCallable("setUserAccount")
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
                        }*/
                }/*else {
                    Log.e("milagre_activity", "${task.exception}")
                    // Exibir mensagem de erro ao criar a conta
                    Log.e(TAG, task.exception?.message ?: "Erro ao criar a conta")
                    Snackbar.make(binding.buttonRegister, "Erro ao criar a conta", Snackbar.LENGTH_LONG).show()
                }*/
            }.addOnFailureListener{ exception ->
                val messageError = when(exception) {
                    is FirebaseAuthWeakPasswordException -> "Digite uma nova senha com no minimo 6 digitos!"
                    is FirebaseAuthInvalidCredentialsException -> "Digite um email valido!"
                    is FirebaseAuthUserCollisionException -> "Esta conja ja existe!"
                    is FirebaseNetworkException -> "Sem conexão com a internet!"
                    else -> "Erro ao cadastrar usuario!"
                }

                Snackbar.make(binding.buttonRegister, messageError, Snackbar.LENGTH_LONG).show()
            }
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_voltar_register -> startActivity(Intent(this, MilagreActivity::class.java))
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