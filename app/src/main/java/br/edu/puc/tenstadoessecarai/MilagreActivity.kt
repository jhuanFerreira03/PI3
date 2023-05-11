package br.edu.puc.tenstadoessecarai

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.edu.puc.tenstadoessecarai.databinding.ActivityMilagreBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.GsonBuilder
import org.json.JSONObject

class MilagreActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()
    private lateinit var functions: FirebaseFunctions
    private lateinit var binding:ActivityMilagreBinding
    private val TAG = "SignUpFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMilagreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        auth = FirebaseAuth.getInstance()
        functions = FirebaseFunctions.getInstance()
        db = FirebaseFirestore.getInstance()

        supportActionBar?.hide()
        binding.imageArrow.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))

        binding.buttonCadastro.setOnClickListener(this)
        binding.addAddressButton.setOnClickListener(this)
        binding.btnVoltar.setOnClickListener(this)
    }

    private fun cadastrarUsuario() {
        // Obtém os valores dos campos de nome, telefone, email e senha
        val nome = binding.editTextNome.text.toString().trim()
        val telefone = binding.editTextTelefone.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()
        val senha = binding.editTextSenha.text.toString().trim()

        // Verifica se o campo nome foi preenchido
        if (TextUtils.isEmpty(nome)) {
            binding.editTextNome.error = "Campo obrigatório"
            return
        }

        // Verifica se o campo telefone foi preenchido
        if (TextUtils.isEmpty(telefone)) {
            binding.editTextTelefone.error = "Campo obrigatório"
            return
        }

        // Verifica se o campo email foi preenchido
        if (TextUtils.isEmpty(email)) {
            binding.editTextEmail.error = "Campo obrigatório"
            return
        }

        // Verifica se o campo senha foi preenchido
        if (TextUtils.isEmpty(senha)) {
            binding.editTextSenha.error = "Campo obrigatório"
            return
        }

        // Chama a função signUpNewAccount para criar uma nova conta de usuário
        signUpNewAccount(nome, telefone, email, senha)
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
                                    binding.buttonCadastro,
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
                    Snackbar.make(binding.buttonCadastro, "Erro ao criar a conta", Snackbar.LENGTH_LONG)
                        .show()
                }
            }
    }

    private fun adicionarEndereco() {
        val additionalAddressesContainer = findViewById<LinearLayout>(R.id.additional_addresses_container)
        val newAddressEditText = EditText(this)
        newAddressEditText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        newAddressEditText.hint = getString(R.string.endereço)
        newAddressEditText.minHeight = resources.getDimensionPixelSize(R.dimen.text_field_size)

        additionalAddressesContainer.addView(newAddressEditText)
    }

    companion object {
        private const val TAG = "MilagreActivity"
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.buttonCadastro -> cadastrarUsuario()
            R.id.add_address_button -> adicionarEndereco()
            R.id.btn_voltar -> {
                startActivity(Intent(this, TelaLogin::class.java))
                finish()
            }
        }
    }

}

data class CustomResponse(val status: String?, val message: String?, val payload: Any?)
