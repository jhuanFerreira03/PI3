package br.edu.puc.sorriso24h.views

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.gson.GsonBuilder
import org.json.JSONObject

class RegisterActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var messaging : FirebaseMessaging
    private lateinit var functions: FirebaseFunctions
    private lateinit var storage : FirebaseStorage

    private lateinit var binding : ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        binding.progressRegister.visibility = View.INVISIBLE

        auth = FirebaseAuth.getInstance()
        functions = FirebaseFunctions.getInstance(Constants.DB.REGIAO)
        db = FirebaseFirestore.getInstance()
        messaging = FirebaseMessaging.getInstance()
        storage = Firebase.storage

        supportActionBar?.hide()

        binding.buttonRegister.setOnClickListener(this)
        binding.btnVoltarRegister.setOnClickListener(this)

        binding.editMiniCurriculo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.textCount.text = s!!.length.toString()
            }
        })
    }
    private fun NewAccount(nome: String, telefone: String, email: String, senha: String, endereco: String, curriculo: String) {
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user!!.uid

                    val token : String = messaging.token.result

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
                    functions.getHttpsCallable(Constants.DB.ADD_DENTIST_FUNCTION)
                        .call(dados)
                        .continueWith{
                            if (SecurityPreferences(this).getString(Constants.KEY_SHARED.ADDRESS_2_REGISTER).toString() != "") {
                                addAd("endereco_2", SecurityPreferences(this).getString(Constants.KEY_SHARED.ADDRESS_2_REGISTER).toString())
                                SecurityPreferences(this).storeString(Constants.KEY_SHARED.ADDRESS_2_REGISTER, "")
                            }
                            if (SecurityPreferences(this).getString(Constants.KEY_SHARED.ADDRESS_3_REGISTER).toString() != "") {
                                addAd("endereco_3", SecurityPreferences(this).getString(Constants.KEY_SHARED.ADDRESS_3_REGISTER).toString())
                                SecurityPreferences(this).storeString(Constants.KEY_SHARED.ADDRESS_3_REGISTER, "")
                            }
                    }
                    //val filearq = Uri.fromFile(file)
                    val riversRef = storage.reference.child("images_user/${auth.currentUser?.uid}")
                    val uploadTask = riversRef.putFile(SecurityPreferences(this).getString(Constants.KEY_SHARED.FT_PERFIL)!!.toUri())
                    uploadTask.addOnFailureListener {
                        Snackbar.make(binding.root, "Imagem enviada!", Snackbar.LENGTH_LONG).setBackgroundTint(Color.GREEN).show()
                    }.addOnSuccessListener {
                        Snackbar.make(binding.root, "Imagem não enviada!", Snackbar.LENGTH_LONG).setBackgroundTint(Color.RED).show()
                    }

                    startActivity(Intent(this, SuccessfulRegisterActivity::class.java))
                }
            }.addOnFailureListener{ exception ->
                val messageError = when(exception) {
                    is FirebaseAuthWeakPasswordException -> Constants.PHRASE.PASSWORD_ERROR_REGISTER
                    is FirebaseAuthInvalidCredentialsException -> Constants.PHRASE.INVALID_EMAIL
                    is FirebaseAuthUserCollisionException -> Constants.PHRASE.USER_ALREADY_EXISTS
                    is FirebaseNetworkException -> Constants.PHRASE.NO_INTERNET
                    else -> Constants.PHRASE.GENERIC_ERROR
                }
                binding.progressRegister.visibility = View.INVISIBLE
                Snackbar.make(binding.buttonRegister, messageError, Snackbar.LENGTH_LONG).show()
            }
    }
    private fun addAd(end : String, value : String){
        val add = hashMapOf(
            end to value
        )
        db.collection(Constants.DB.DENTISTAS)
            .whereEqualTo(Constants.DB.FIELD.UID, auth.currentUser!!.uid)
            .get()
            .addOnCompleteListener {
                val doc : DocumentSnapshot = it.result.documents[0]
                val docId : String = doc.id

                db.collection(Constants.DB.DENTISTAS)
                    .document(docId)
                    .update(add as Map<String, Any>)
                    .addOnCompleteListener {}
            }.addOnFailureListener{
            }
    }
    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_voltar_register -> startActivity(Intent(this, PhotoRegisterActivity::class.java))
            R.id.button_register -> {
                if(binding.editMiniCurriculo.length() == 0){
                    Snackbar.make(binding.buttonRegister, Constants.PHRASE.MINI_CURR, Snackbar.LENGTH_LONG)
                        .setBackgroundTint(Color.rgb(229,0,37))
                        .show()
                    return
                }
                binding.progressRegister.visibility = View.VISIBLE
                SecurityPreferences(this).storeString(Constants.KEY_SHARED.CURRICULUM, binding.editMiniCurriculo.text.toString())

                NewAccount(SecurityPreferences(this).getString(Constants.KEY_SHARED.NAME_REGISTER).toString(),
                    SecurityPreferences(this).getString(Constants.KEY_SHARED.PHONE_NUMBER_REGISTER).toString(),
                    SecurityPreferences(this).getString(Constants.KEY_SHARED.EMAIL_REGISTER).toString(),
                    SecurityPreferences(this).getString(Constants.KEY_SHARED.PASSWORD_REGISTER).toString(),
                    SecurityPreferences(this).getString(Constants.KEY_SHARED.ADDRESS_1_REGISTER).toString(),
                    SecurityPreferences(this).getString(Constants.KEY_SHARED.CURRICULUM).toString())
            }
        }
    }
}