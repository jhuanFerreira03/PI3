package br.edu.puc.sorriso24h.views

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.renderscript.ScriptGroup.Input
import android.text.InputType
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivityAccountDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import br.edu.puc.sorriso24h.infra.Constants
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import io.grpc.InternalChannelz.Security
import org.w3c.dom.Text
import java.io.File
import kotlin.math.round

class AccountDetailsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityAccountDetailsBinding

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var messaging : FirebaseMessaging
    private lateinit var storage : FirebaseStorage

    private lateinit var end1List :List<String>
    private lateinit var end2List :List<String>
    private lateinit var end3List :List<String>

    private lateinit var attField : String

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        messaging = FirebaseMessaging.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.btnVoltarRegister.setOnClickListener(this)
        binding.textAddress1.setOnClickListener(this)
        binding.textAddress2.setOnClickListener(this)
        binding.textAddress3.setOnClickListener(this)
        binding.imageButtonEditName.setOnClickListener(this)
        binding.imageButtonEditEmail.setOnClickListener(this)
        binding.imageButtonEditTelefone.setOnClickListener(this)
        binding.buttonAtt.setOnClickListener(this)
        binding.buttonCancelarAtt.setOnClickListener(this)
        binding.imagePhoto.setOnClickListener(this)
        binding.imagePhotoEdit.setOnClickListener(this)
        binding.textEditAddress.setOnClickListener(this)
        binding.imageButtonEditAddress.setOnClickListener(this)

        countStars()

        setInfos()
    }
    private fun setInfos(){
        val file : File = File.createTempFile("tempfile", ".jpg")
        storage.getReference("images_user/${auth.currentUser!!.uid}").getFile(file).addOnSuccessListener {
            binding.imagePhoto.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            binding.imagePhoto.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
            binding.progressMain.visibility = View.INVISIBLE
        }
        db.collection(Constants.DB.DENTISTAS)
            .whereEqualTo(Constants.DB.FIELD.UID, auth.currentUser!!.uid)
            .get()
            .addOnCompleteListener {
                doc ->
                binding.textAccountDetailNome.text = doc.result.documents[0].get(Constants.DB.FIELD.NAME_DB).toString()
                binding.textAccountDetailEmail.text = doc.result.documents[0].get(Constants.DB.FIELD.EMAIL_DB).toString()
                binding.textAccountDetailTelefone.text = doc.result.documents[0].get(Constants.DB.FIELD.PHONE).toString()

                try {
                    end1List = doc.result.documents[0].get("endereco").toString().split(',')
                    if (end1List[0] != "null" && end1List[0] != "") {
                        binding.textAddress1.text = end1List[0]
                    }else{
                        //binding.textAddress1.isEnabled = false
                    }
                }catch (_:Exception){
                //binding.textAddress1.isEnabled = false}
                }

                try {
                    end2List = doc.result.documents[0].get("endereco_2").toString().split(',')
                    if(end2List[0] != "null" && end2List[0] != "") {
                        binding.textAddress2.text = end2List[0]
                    }else {
                        //binding.textAddress2.isEnabled = false
                    }
                }catch (_:Exception){
                //binding.textAddress2.isEnabled = false}
                }
                try {
                    end3List = doc.result.documents[0].get("endereco_3").toString().split(',')
                    if (end3List[0] != "null" && end3List[0] != "") {
                        binding.textAddress3.text = end3List[0]
                    }else{
                        //binding.textAddress3.isEnabled = false
                    }
                }catch (e:Exception){
                //binding.textAddress3.isEnabled = false }
                }
            }
    }
    private fun setAddress(addrees : String) {
        if (addrees == Constants.KEY_SHARED.ADDRESS_1_REGISTER && binding.textAddress1.text != "Endereço 1") {
            binding.textAddress1.setTextColor(ContextCompat.getColor(this, R.color.second))
            binding.textAddress1.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            binding.textAccountDetailStreet.text = end1List[1]
            binding.textAccountDetailNumber.text = end1List[2]
            binding.textAccountDetailNeighborhood.text = end1List[3]
            binding.textAccountDetailCEP.text = end1List[4]
            binding.textAccountDetailCity.text = end1List[5]
            binding.textAccountDetailState.text = end1List[6]

            binding.textAddress2.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.textAddress3.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.textAddress2.setBackgroundColor(ContextCompat.getColor(this, R.color.second))
            binding.textAddress3.setBackgroundColor(ContextCompat.getColor(this, R.color.second))
        }
        else if (addrees == Constants.KEY_SHARED.ADDRESS_2_REGISTER) {
            if (binding.textAddress2.text.toString() != "Endereço 2") {
                binding.textAddress2.setTextColor(ContextCompat.getColor(this, R.color.second))
                binding.textAddress2.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                binding.textAccountDetailStreet.text = end2List[1]
                binding.textAccountDetailNumber.text = end2List[2]
                binding.textAccountDetailNeighborhood.text = end2List[3]
                binding.textAccountDetailCEP.text = end2List[4]
                binding.textAccountDetailCity.text = end2List[5]
                binding.textAccountDetailState.text = end2List[6]

                binding.textAddress1.setTextColor(ContextCompat.getColor(this, R.color.white))
                binding.textAddress3.setTextColor(ContextCompat.getColor(this, R.color.white))
                binding.textAddress1.setBackgroundColor(ContextCompat.getColor(this, R.color.second))
                binding.textAddress3.setBackgroundColor(ContextCompat.getColor(this, R.color.second))
            }else {
                Snackbar.make(binding.root, "Não possui um segundo endereço registrado!", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.RED)
                    .show()
                return
            }
        }
        else if (addrees == Constants.KEY_SHARED.ADDRESS_3_REGISTER) {
            if (binding.textAddress3.text.toString().trim() != "Endereço 3") {
                binding.textAddress3.setTextColor(ContextCompat.getColor(this, R.color.second))
                binding.textAddress3.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                binding.textAccountDetailStreet.text = end3List[1]
                binding.textAccountDetailNumber.text = end3List[2]
                binding.textAccountDetailNeighborhood.text = end3List[3]
                binding.textAccountDetailCEP.text = end3List[4]
                binding.textAccountDetailCity.text = end3List[5]
                binding.textAccountDetailState.text = end3List[6]

                binding.textAddress1.setTextColor(ContextCompat.getColor(this, R.color.white))
                binding.textAddress2.setTextColor(ContextCompat.getColor(this, R.color.white))
                binding.textAddress1.setBackgroundColor(ContextCompat.getColor(this, R.color.second))
                binding.textAddress2.setBackgroundColor(ContextCompat.getColor(this, R.color.second))
            }else {
                Snackbar.make(binding.root, "Não possui um terceiro endereço registrado!", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.RED)
                    .show()
                return
            }
        }
    }
    private fun openEditAtt(field: String) {
        binding.editAtt.hint = "Novo $field"

        attField = field

        when (field) {
            Constants.DB.FIELD.NAME_DB -> {
                binding.editAtt.inputType = InputType.TYPE_CLASS_TEXT
                binding.editAtt.setText(binding.textAccountDetailNome.text)
            }
            Constants.DB.FIELD.EMAIL_DB -> {
                binding.editAtt.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                binding.editAtt.setText(binding.textAccountDetailEmail.text)
            }
            Constants.DB.FIELD.PHONE -> {
                binding.editAtt.inputType = InputType.TYPE_CLASS_NUMBER
                binding.editAtt.setText(binding.textAccountDetailTelefone.text)
            }
        }

        binding.editAtt.visibility = View.VISIBLE
        binding.buttonAtt.visibility = View.VISIBLE
        binding.buttonCancelarAtt.visibility = View.VISIBLE

        binding.textAccountDetailNome.visibility = View.INVISIBLE
        binding.textAccountDetailEmail.visibility = View.INVISIBLE
        binding.textAccountDetailTelefone.visibility = View.INVISIBLE
        binding.textView13.visibility = View.INVISIBLE
        binding.textView14.visibility = View.INVISIBLE
        binding.textView15.visibility = View.INVISIBLE
        binding.imageButtonEditName.visibility = View.INVISIBLE
        binding.imageButtonEditEmail.visibility = View.INVISIBLE
        binding.imageButtonEditTelefone.visibility = View.INVISIBLE
    }
    private fun closeEditAtt() {
        binding.editAtt.setText("")

        binding.editAtt.visibility = View.INVISIBLE
        binding.buttonAtt.visibility = View.INVISIBLE
        binding.buttonCancelarAtt.visibility = View.INVISIBLE

        binding.textAccountDetailNome.visibility = View.VISIBLE
        binding.textAccountDetailEmail.visibility = View.VISIBLE
        binding.textAccountDetailTelefone.visibility = View.VISIBLE
        binding.textView13.visibility = View.VISIBLE
        binding.textView14.visibility = View.VISIBLE
        binding.textView15.visibility = View.VISIBLE
        binding.imageButtonEditName.visibility = View.VISIBLE
        binding.imageButtonEditEmail.visibility = View.VISIBLE
        binding.imageButtonEditTelefone.visibility = View.VISIBLE
    }
    private fun updateField(field: String) {
        if (binding.editAtt.text.trim().isEmpty()) {
            binding.editAtt.error = Constants.PHRASE.EMPTY_FIELD
            return
        }
        val dados = hashMapOf(
            field to binding.editAtt.text.toString().trim().lowercase()
        )

        db.collection(Constants.DB.DENTISTAS)
            .whereEqualTo(Constants.DB.FIELD.UID, auth.currentUser!!.uid)
            .get()
            .addOnCompleteListener {
                val doc : DocumentSnapshot = it.result.documents[0]
                val docId : String = doc.id

                db.collection(Constants.DB.DENTISTAS)
                    .document(docId)
                    .update(dados as Map<String, Any>)
                    .addOnCompleteListener {
                        closeEditAtt()
                        setInfos()
                        Snackbar.make(binding.root, "$field atualizado com sucesso!".uppercase(), Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.rgb(0,191,54))
                            .show()
                    }
            }
    }
    private val cameraProviderResult = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if(it){
            startActivity(Intent(this, CameraActivity::class.java))
        } else {
            Snackbar.make(binding.root, "É necessário conceder permissão de camera!", Toast.LENGTH_LONG)
                .setBackgroundTint(Color.rgb(229,0,37))
                .show()
        }
    }
    private fun setVisibleEdit() {
        if(binding.imagePhotoEdit.isVisible) {
            binding.imagePhotoEdit.visibility = View.INVISIBLE
        }
        else {
            binding.imagePhotoEdit.visibility = View.VISIBLE
        }
    }
    private fun countStars() {
        var sum = 0.0
        var count = 0.0
        db.collection(Constants.DB.DENTISTAS).whereEqualTo(Constants.DB.FIELD.UID, auth.currentUser!!.uid).get().addOnCompleteListener {
            db.collection(Constants.DB.ATENDIMENTOS).whereEqualTo(Constants.DB.FIELD.DENTISTA, it.result.documents[0].id).get().addOnCompleteListener {
                docs ->
                for (doc : DocumentSnapshot in docs.result.documents) {
                    val docc = doc.data as HashMap<String?, Any?>
                    if (docc.containsKey("estrelasProfissional") && docc["estrelasProfissional"] != null && docc["estrelasProfissional"] != "") {
                        val aux = docc["estrelasProfissional"].toString()
                        sum += aux.toDouble()
                        count++
                    }
                }
                if(count != 0.0) {
                    val x = sum / count
                    binding.imageCount.text = round(x).toString()
                }
            }
        }
    }
    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_voltar_register -> {
                startActivity(Intent(this, UserActivity::class.java))
                finish()
            }
            R.id.text_address1 -> setAddress(Constants.KEY_SHARED.ADDRESS_1_REGISTER)
            R.id.text_address2 -> setAddress(Constants.KEY_SHARED.ADDRESS_2_REGISTER)
            R.id.text_address3 -> setAddress(Constants.KEY_SHARED.ADDRESS_3_REGISTER)
            R.id.imageButton_editName -> openEditAtt(Constants.DB.FIELD.NAME_DB)
            //R.id.imageButton_editEmail -> openEditAtt(Constants.DB.FIELD.EMAIL_DB)
            R.id.imageButton_editTelefone -> openEditAtt(Constants.DB.FIELD.PHONE)
            R.id.button_att -> updateField(attField)
            R.id.button_cancelarAtt -> closeEditAtt()
            R.id.image_photo -> {
                SecurityPreferences(this).storeString(Constants.KEY_SHARED.PHOTO_VIEW, "images_user/${auth.currentUser!!.uid}")
                SecurityPreferences(this).storeString(Constants.KEY_SHARED.PHOTO_VIEW_DECIDER, "account")
                startActivity(Intent(this, PhotoViewActivity::class.java))
            }
            R.id.image_photo_edit -> {
                SecurityPreferences(this).storeString(Constants.KEY_SHARED.PHOTO, Constants.CAMERA.FRONT)
                SecurityPreferences(applicationContext).storeString(Constants.KEY_SHARED.DECIDER_PICTURE, "detail")
                cameraProviderResult.launch(android.Manifest.permission.CAMERA)
            }
            R.id.imageButtonEditAddress, R.id.text_editAddress -> {
                startActivity(Intent(this, AddressEditActivity::class.java))
            }
        }
    }
}
