package br.edu.puc.sorriso24h.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import br.edu.puc.sorriso24h.R
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
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.GsonBuilder
import org.json.JSONObject

class RegisterActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var messaging : FirebaseMessaging
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
        binding.progressRegister.visibility = View.INVISIBLE

        auth = FirebaseAuth.getInstance()
        functions = FirebaseFunctions.getInstance("southamerica-east1")
        db = FirebaseFirestore.getInstance()
        messaging = FirebaseMessaging.getInstance()

        supportActionBar?.hide()

        binding.buttonRegister.setOnClickListener(this)
        binding.btnVoltarRegister.setOnClickListener(this)
    }

    private fun NewAccount(nome: String, telefone: String, email: String, senha: String, endereco: String, curriculo: String) {
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user!!.uid

                    lateinit var token : String

                    token = messaging.token.result

                    val dados = hashMapOf(
                        "uid" to uid,
                        "nome" to nome,
                        "email" to email,
                        "telefone" to telefone,
                        "endereco" to endereco,
                        "curriculo" to curriculo,
                        "fcmToken" to token,
                        "status" to false,
                    )

                    /*auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener { log ->
                        if(log.isSuccessful){
                            db.collection("User").document().set(dados).addOnCompleteListener { add ->
                                Log.d("db", "Sucesso meu bom!")
                            }.addOnFailureListener{ exception ->
                                binding.textError.setText(exception.toString())
                                Snackbar.make(binding.buttonRegister, "ih rapaz..."+ exception, Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }*/

                    functions.getHttpsCallable("addUsers").call(dados).continueWith { task1 ->
                        val json = JSONObject(task1.result?.data as String)
                        val status = json.getString("status")
                        val message = json.getString("message")
                    }
                    startActivity(Intent(this, SuccessfulRegisterActivity::class.java))
                    finish()
                }
            }.addOnFailureListener{ exception ->
                val messageError = when(exception) {
                    is FirebaseAuthWeakPasswordException -> "Digite uma nova senha com no minimo 6 digitos!"
                    is FirebaseAuthInvalidCredentialsException -> "Digite um email valido!"
                    is FirebaseAuthUserCollisionException -> "Esta conta ja existe!"
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
                if(binding.editMiniCurriculo.length() == 0){
                    Snackbar.make(binding.buttonRegister, "Faça um mini curriculo!", Snackbar.LENGTH_LONG).show()
                    return
                }
                binding.progressRegister.visibility = View.VISIBLE
                SecurityPreferences(this).storeString(Constants.KEY.CURRICULUM, binding.editMiniCurriculo.text.toString())

                NewAccount(SecurityPreferences(this).getString(Constants.KEY.NAME_REGISTER).toString(),
                    SecurityPreferences(this).getString(Constants.KEY.PHONE_NUMBER_REGISTER).toString(),
                    SecurityPreferences(this).getString(Constants.KEY.EMAIL_REGISTER).toString(),
                    SecurityPreferences(this).getString(Constants.KEY.PASSWORD_REGISTER).toString(),
                    SecurityPreferences(this).getString(Constants.KEY.ADDRESS_1_REGISTER).toString(),
                    SecurityPreferences(this).getString(Constants.KEY.CURRICULUM).toString())
            }
        }
    }
}