package br.edu.puc.tenstadoessecarai

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.marginEnd
import br.edu.puc.tenstadoessecarai.databinding.ActivityMilagreBinding
import br.edu.puc.tenstadoessecarai.infra.Constants
import br.edu.puc.tenstadoessecarai.infra.SecurityPreferences
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.api.Distribution.BucketOptions.Linear
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
    private var countAddress : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMilagreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        auth = FirebaseAuth.getInstance()
        functions = FirebaseFunctions.getInstance()
        db = FirebaseFirestore.getInstance()

        supportActionBar?.hide()
        binding.imageArrowBack.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
        binding.imageArrowNext.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))

        binding.btnAvancarRegister.setOnClickListener(this)
        binding.btnVoltarRegister.setOnClickListener(this)
        binding.addAddressButton.setOnClickListener(this)
    }

    private fun userRegister() {
        // Obtém os valores dos campos de nome, telefone, email e senha
        val nome = binding.editTextNome.text.toString().trim()
        val telefone = binding.editTextTelefone.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()
        val senha = binding.editTextSenha.text.toString().trim()


        // Verifica se o campo nome foi preenchido
        if (TextUtils.isEmpty(nome)) {
            binding.editTextNome.error = Constants.PHRASE.EMPTY_FIELD
            return
        }

        // Verifica se o campo email foi preenchido
        if (TextUtils.isEmpty(email)) {
            binding.editTextEmail.error = Constants.PHRASE.EMPTY_FIELD
            return
        }

        // Verifica se o campo senha foi preenchido
        if (TextUtils.isEmpty(senha)) {
            binding.editTextSenha.error = Constants.PHRASE.EMPTY_FIELD
            return
        }

        // Verifica se o campo telefone foi preenchido
        if (TextUtils.isEmpty(telefone)) {
            binding.editTextTelefone.error = Constants.PHRASE.EMPTY_FIELD
            return
        }

        if(countAddress == 0){
            Toast.makeText(this, Constants.PHRASE.NO_ADDRESS, Toast.LENGTH_SHORT).show()
            return
        }

        SecurityPreferences(this).storeString(Constants.KEY.EMAIL_REGISTER, binding.editTextEmail.text.toString().trim().lowercase())
        SecurityPreferences(this).storeString(Constants.KEY.PASSWORD_REGISTER, binding.editTextSenha.text.toString().trim().lowercase())
        SecurityPreferences(this).storeString(Constants.KEY.PHONE_NUMBER_REGISTER, binding.editTextTelefone.text.toString().trim().lowercase())
        SecurityPreferences(this).storeString(Constants.KEY.NAME_REGISTER, binding.editTextNome.text.toString().trim().lowercase())

        startActivity(Intent(this, RegisterActivity::class.java))

        // Chama a função signUpNewAccount para criar uma nova conta de usuário
        //signUpNewAccount(nome, telefone, email, senha)
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
                                    binding.btnAvancarRegister,
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
                    Snackbar.make(binding.btnAvancarRegister, "Erro ao criar a conta", Snackbar.LENGTH_LONG)
                        .show()
                }
            }
    }


    private fun addAddress() {
        if(binding.addressName.text.toString().trim().isEmpty()){
            binding.addressName.error = Constants.PHRASE.EMPTY_FIELD
            return
        }
        if(binding.addressStreet.text.toString().trim().isEmpty()){
            binding.addressStreet.error = Constants.PHRASE.EMPTY_FIELD
            return
        }
        if(binding.addressNumber.text.toString().trim().isEmpty()){
            binding.addressNumber.error = Constants.PHRASE.EMPTY_FIELD
            return
        }
        if(binding.addressBairro.text.toString().trim().isEmpty()){
            binding.addressBairro.error = Constants.PHRASE.EMPTY_FIELD
            return
        }
        if(binding.addressCidade.text.toString().trim().isEmpty()){
            binding.addressCidade.error = Constants.PHRASE.EMPTY_FIELD
            return
        }
        if(binding.addressEstado.text.toString().trim().isEmpty()){
            binding.addressEstado.error = Constants.PHRASE.EMPTY_FIELD
            return
        }

        val address: String = binding.addressName.text.toString().trim().lowercase() + "," +
                binding.addressStreet.text.toString().trim().lowercase() + "," +
                binding.addressNumber.text.toString().trim().lowercase() + "," +
                binding.addressBairro.text.toString().trim().lowercase() + "," +
                binding.addressCidade.text.toString().trim().lowercase() + "," +
                binding.addressEstado.text.toString().trim().lowercase()

        when(countAddress) {
            0 -> SecurityPreferences(this).storeString(Constants.KEY.ADDRESS_1_REGISTER, address)
            1 -> SecurityPreferences(this).storeString(Constants.KEY.ADDRESS_2_REGISTER, address)
            2 -> SecurityPreferences(this).storeString(Constants.KEY.ADDRESS_3_REGISTER, address)
        }

        val additionalAddressesContainer = findViewById<LinearLayout>(R.id.additional_addresses_container)
        val newAddressEditText = TextView(this)

        newAddressEditText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )
        newAddressEditText.hint = getString(R.string.endereço)
        newAddressEditText.minHeight = resources.getDimensionPixelSize(R.dimen.text_field_size)
        newAddressEditText.text = binding.addressName.text.toString().trim()
        newAddressEditText.textSize = 24f
        newAddressEditText.setTextColor(getResources().getColor(R.color.black))
        newAddressEditText.id = View.generateViewId()
        Toast.makeText(this, newAddressEditText.id.toString(), Toast.LENGTH_SHORT).show()

        additionalAddressesContainer.addView(newAddressEditText)
        countAddress += 1

        binding.addressName.setText("")
        binding.addressStreet.setText("")
        binding.addressNumber.setText("")
        binding.addressBairro.setText("")
        binding.addressCidade.setText("")
        binding.addressEstado.setText("")

        Toast.makeText(this, SecurityPreferences(this).getString(Constants.KEY.ADDRESS_1_REGISTER), Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MilagreActivity"
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_avancar_register -> userRegister()
            R.id.add_address_button -> {
                if(countAddress < 3) {
                    addAddress()
                }else{
                    Toast.makeText(this, Constants.PHRASE.LIMIT_ADDRESS, Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btn_voltar_register -> {
                startActivity(Intent(this, TelaLogin::class.java))
                finish()
            }
        }
    }

}

data class CustomResponse(val status: String?, val message: String?, val payload: Any?)
