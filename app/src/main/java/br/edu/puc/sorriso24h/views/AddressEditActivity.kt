package br.edu.puc.sorriso24h.views

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivityAddressEditBinding
import br.edu.puc.sorriso24h.infra.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class AddressEditActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding : ActivityAddressEditBinding

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    private lateinit var end1List :List<String>
    private lateinit var end2List :List<String>
    private lateinit var end3List :List<String>

    private lateinit var attAdd : String
    private lateinit var attField : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageArrowBack.setColorFilter(ContextCompat.getColor(this, R.color.second))

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.textAddress1.setOnClickListener(this)
        binding.textAddress2.setOnClickListener(this)
        binding.textAddress3.setOnClickListener(this)
        binding.buttonConfirmExc.setOnClickListener(this)
        binding.buttonCancelarExc.setOnClickListener(this)
        binding.btnVoltarRegister.setOnClickListener(this)
        binding.deleteAddressButton.setOnClickListener(this)
        binding.addAddressButton.setOnClickListener(this)

        setInfos()
    }

    private fun setInfos() {
        db.collection(Constants.DB.DENTISTAS)
            .whereEqualTo("uid", auth.currentUser!!.uid)
            .get()
            .addOnCompleteListener {
                    doc ->
                try {
                    end1List = doc.result.documents[0].get("endereco").toString().split(',')
                    if (end1List[0] != "null" && end1List[0] != "") {
                        binding.textAddress1.text = end1List[0]
                    }else{
                        binding.textAddress1.visibility = View.INVISIBLE
                    }
                }catch (_:Exception){binding.textAddress1.visibility = View.INVISIBLE}
                try {
                    end2List = doc.result.documents[0].get("endereco_2").toString().split(',')
                    if(end2List[0] != "null" && end2List[0] != "") {
                        binding.textAddress2.text = end2List[0]
                    }else {
                        binding.textAddress2.visibility = View.INVISIBLE
                    }
                }catch (_:Exception){binding.textAddress2.visibility = View.INVISIBLE}
                try {
                    end3List = doc.result.documents[0].get("endereco_3").toString().split(',')
                    if (end3List[0] != "null" && end3List[0] != "") {
                        binding.textAddress3.text = end3List[0]
                    }else{
                        binding.textAddress3.visibility = View.INVISIBLE
                    }
                }catch (e:Exception){binding.textAddress3.visibility = View.INVISIBLE }
            }
    }
    private fun setAddress(addrees : String) {
        if (addrees == Constants.KEY_SHARED.ADDRESS_1_REGISTER && binding.textAddress1.text != "Endereço 1") {
            attAdd = end1List[0]
            attField = "endereco"
            binding.textAddress1.setTextColor(ContextCompat.getColor(this, R.color.second))
            binding.textAddress1.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))

            binding.addressName.setText(end1List[0])
            binding.addressStreet.setText(end1List[1])
            binding.addressNumber.setText(end1List[2])
            binding.addressBairro.setText(end1List[3])
            binding.addressCep.setText(end1List[4])
            binding.addressCidade.setText(end1List[5])
            binding.addressEstado.setText(end1List[6])

            binding.textAddress2.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.textAddress3.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.textAddress2.setBackgroundColor(ContextCompat.getColor(this, R.color.second))
            binding.textAddress3.setBackgroundColor(ContextCompat.getColor(this, R.color.second))

            return
        }
        else if (addrees == Constants.KEY_SHARED.ADDRESS_2_REGISTER) {
            if (binding.textAddress2.text.toString() != "Endereço 2") {
                attAdd = end2List[0]
                attField = "endereco_2"
                binding.textAddress2.setTextColor(ContextCompat.getColor(this, R.color.second))
                binding.textAddress2.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))

                binding.addressName.setText(end2List[0])
                binding.addressStreet.setText(end2List[1])
                binding.addressNumber.setText(end2List[2])
                binding.addressBairro.setText(end2List[3])
                binding.addressCep.setText(end2List[4])
                binding.addressCidade.setText(end2List[5])
                binding.addressEstado.setText(end2List[6])

                binding.textAddress1.setTextColor(ContextCompat.getColor(this, R.color.white))
                binding.textAddress3.setTextColor(ContextCompat.getColor(this, R.color.white))
                binding.textAddress1.setBackgroundColor(ContextCompat.getColor(this, R.color.second))
                binding.textAddress3.setBackgroundColor(ContextCompat.getColor(this, R.color.second))

                return
            }
        }
        else if (addrees == Constants.KEY_SHARED.ADDRESS_3_REGISTER) {
            if (binding.textAddress3.text.toString().trim() != "Endereço 3") {
                attAdd = end3List[0]
                attField = "endereco_3"
                binding.textAddress3.setTextColor(ContextCompat.getColor(this, R.color.second))
                binding.textAddress3.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))

                binding.addressName.setText(end3List[0])
                binding.addressStreet.setText(end3List[1])
                binding.addressNumber.setText(end3List[2])
                binding.addressBairro.setText(end3List[3])
                binding.addressCep.setText(end3List[4])
                binding.addressCidade.setText(end3List[5])
                binding.addressEstado.setText(end3List[6])

                binding.textAddress1.setTextColor(ContextCompat.getColor(this, R.color.white))
                binding.textAddress2.setTextColor(ContextCompat.getColor(this, R.color.white))
                binding.textAddress1.setBackgroundColor(ContextCompat.getColor(this, R.color.second))
                binding.textAddress2.setBackgroundColor(ContextCompat.getColor(this, R.color.second))

                return
            }
        }
        binding.addressName.setText("")
        binding.addressStreet.setText("")
        binding.addressNumber.setText("")
        binding.addressBairro.setText("")
        binding.addressCep.setText("")
        binding.addressCidade.setText("")
        binding.addressEstado.setText("")
    }
    @SuppressLint("SetTextI18n")
    private fun openPop() {
        binding.deleteAddressButton.visibility = View.INVISIBLE
        binding.addAddressButton.visibility = View.INVISIBLE
        binding.RelativeConfirm.visibility = View.VISIBLE
        binding.textAddressConfirm.text = "${attAdd.uppercase()}?"
        binding.textView10.visibility = View.INVISIBLE
    }
    private fun closedPop() {
        binding.deleteAddressButton.visibility = View.VISIBLE
        binding.addAddressButton.visibility = View.VISIBLE
        binding.RelativeConfirm.visibility = View.INVISIBLE
        binding.textView10.visibility = View.VISIBLE
    }
    private fun verifyAddress():Boolean{
        if(binding.addressName.text.toString().trim().isEmpty()){
            binding.addressName.error = Constants.PHRASE.EMPTY_FIELD
            return false
        }
        if(binding.addressStreet.text.toString().trim().isEmpty()){
            binding.addressStreet.error = Constants.PHRASE.EMPTY_FIELD
            return false
        }
        if(binding.addressNumber.text.toString().trim().isEmpty()){
            binding.addressNumber.error = Constants.PHRASE.EMPTY_FIELD
            return false
        }
        if(binding.addressBairro.text.toString().trim().isEmpty()){
            binding.addressBairro.error = Constants.PHRASE.EMPTY_FIELD
            return false
        }
        if(binding.addressCep.text.toString().trim().isEmpty()){
            binding.addressCep.error = Constants.PHRASE.EMPTY_FIELD
            return false
        }
        if(binding.addressCep.text.toString().trim().length != 8){
            binding.addressCep.error = Constants.PHRASE.MIN_LENGHT
            return false
        }
        if(binding.addressCidade.text.toString().trim().isEmpty()){
            binding.addressCidade.error = Constants.PHRASE.EMPTY_FIELD
            return false
        }
        if(binding.addressEstado.text.toString().trim().isEmpty()){
            binding.addressEstado.error = Constants.PHRASE.EMPTY_FIELD
            return false
        }
        return true
    }
    private fun updateAddress(delete: Boolean) {
        var address = ""
        if (!delete) {
            if (!verifyAddress()) return
            address = binding.addressName.text.toString().trim().lowercase() + "," +
                    binding.addressStreet.text.toString().trim().lowercase() + "," +
                    binding.addressNumber.text.toString().trim().lowercase() + "," +
                    binding.addressBairro.text.toString().trim().lowercase() + "," +
                    binding.addressCep.text.toString().trim().lowercase() + "," +
                    binding.addressCidade.text.toString().trim().lowercase() + "," +
                    binding.addressEstado.text.toString().trim().lowercase()
        }
        db.collection(Constants.DB.DENTISTAS).whereEqualTo(Constants.DB.FIELD.UID, auth.currentUser!!.uid).get().addOnCompleteListener {
            db.collection(Constants.DB.DENTISTAS).document(it.result.documents[0].id).update(attField, address).addOnCompleteListener {
                Snackbar.make(binding.root, "Endereço atualizado com sucesso!", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.GREEN)
                    .show()
                setInfos()
            }.addOnFailureListener { error ->
                Snackbar.make(binding.root, "Falha ao atualizar o Endereço! " + error.message, Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.RED)
                    .show()
            }
        }
    }
    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_voltar_register -> {
                startActivity(Intent(this, AccountDetailsActivity::class.java))
                finish()
            }
            R.id.text_address1 -> setAddress(Constants.KEY_SHARED.ADDRESS_1_REGISTER)
            R.id.text_address2 -> setAddress(Constants.KEY_SHARED.ADDRESS_2_REGISTER)
            R.id.text_address3 -> setAddress(Constants.KEY_SHARED.ADDRESS_3_REGISTER)
            R.id.add_address_button -> updateAddress(false)
            R.id.delete_address_button -> openPop()
            R.id.button_cancelarExc -> closedPop()
            R.id.button_confirmExc -> updateAddress(true)
        }
    }
}