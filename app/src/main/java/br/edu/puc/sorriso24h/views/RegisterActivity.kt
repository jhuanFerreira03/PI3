package br.edu.puc.sorriso24h.views

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
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
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()
    private lateinit var functions: FirebaseFunctions
    private lateinit var storage : FirebaseStorage

    private lateinit var binding : ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        binding.imageArrowBack.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
        binding.progressRegister.visibility = View.INVISIBLE

        auth = FirebaseAuth.getInstance()
        functions = FirebaseFunctions.getInstance(Constants.DB.REGIAO)
        db = FirebaseFirestore.getInstance()
        messaging = FirebaseMessaging.getInstance()
        storage = Firebase.storage

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
                        .continueWith{ task1 ->
                        val json = JSONObject(task1.result?.data as String)
                        //val status = json.getString("status")
                        //val message = json.getString("message")
                    }
                    /*if (SecurityPreferences(this).getString(Constants.KEY_SHARED.ADDRESS_2_REGISTER).toString() != "") {
                        val end2 = hashMapOf(
                            "endereco_2" to SecurityPreferences(this).getString(Constants.KEY_SHARED.ADDRESS_2_REGISTER).toString()
                        )
                        db.collection(Constants.DB.DENTISTAS)
                            .whereEqualTo(Constants.DB.FIELD.UID, auth.currentUser!!.uid)
                            .get()
                            .addOnCompleteListener {
                                val doc : DocumentSnapshot = it.result.documents[0]
                                val docId : String = doc.id

                                db.collection(Constants.DB.DENTISTAS)
                                    .document(docId)
                                    .update(end2 as Map<String, Any>)
                                    .addOnCompleteListener {}
                            }.addOnFailureListener{
                            }
                    }
                    if (SecurityPreferences(this).getString(Constants.KEY_SHARED.ADDRESS_3_REGISTER).toString() != "") {
                        val end3 = hashMapOf(
                            "endereco_3" to SecurityPreferences(this).getString(Constants.KEY_SHARED.ADDRESS_3_REGISTER).toString()
                        )
                        db.collection(Constants.DB.DENTISTAS)
                            .whereEqualTo(Constants.DB.FIELD.UID, auth.currentUser!!.uid)
                            .get()
                            .addOnCompleteListener {
                                val doc : DocumentSnapshot = it.result.documents[0]
                                val docId : String = doc.id

                                db.collection(Constants.DB.DENTISTAS)
                                    .document(docId)
                                    .update(end3 as Map<String, Any>)
                                    .addOnCompleteListener {}
                            }
                    }*/
                    //val filearq = Uri.fromFile(file)
                    val riversRef = storage.reference.child("images_user/${auth.currentUser?.uid}")
                    val uploadTask = riversRef.putFile(SecurityPreferences(this).getString("ft_perfil")!!.toUri())
                    uploadTask.addOnFailureListener {
                        Snackbar.make(binding.root, "Imagem enviada!", Snackbar.LENGTH_LONG).setBackgroundTint(Color.GREEN).show()
                    }.addOnSuccessListener { taskSnapshot ->
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
                Snackbar.make(binding.buttonRegister, messageError, Snackbar.LENGTH_LONG).show()
            }
    }
    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_voltar_register -> startActivity(Intent(this, PhotoRegisterActivity::class.java))
            R.id.button_register -> {
                if(binding.editMiniCurriculo.length() == 0){
                    Snackbar.make(binding.buttonRegister, Constants.PHRASE.MINI_CURR, Snackbar.LENGTH_LONG).show()
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