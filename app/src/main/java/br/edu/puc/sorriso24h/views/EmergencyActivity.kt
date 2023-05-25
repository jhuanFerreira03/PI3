package br.edu.puc.sorriso24h.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.puc.sorriso24h.Adapter.MyAdapter
import br.edu.puc.sorriso24h.Adapter.User
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivityEmergencyBinding
import br.edu.puc.sorriso24h.databinding.ActivityEmergencyDetailBinding
import br.edu.puc.sorriso24h.infra.Constants
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import br.edu.puc.sorriso24h.listener.ListListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class EmergencyActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityEmergencyBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var messaging : FirebaseMessaging

    private lateinit var recyclerView : RecyclerView
    private lateinit var arrayList : ArrayList<User>
    private lateinit var myAdapter : MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        messaging = FirebaseMessaging.getInstance()

        supportActionBar?.hide()

        binding.imageArrowBack.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
        binding.btnVoltarRegister.setOnClickListener(this)

        recyclerView = findViewById(R.id.recycle)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        arrayList = arrayListOf()

        myAdapter = MyAdapter(this, arrayList)

        recyclerView.adapter = myAdapter

        val listener = object : ListListener{
            override fun onClick(adapterPosition: Int) {
                SecurityPreferences(myAdapter.context).storeString(Constants.KEY.ARRAY_NAME, arrayList.get(adapterPosition).nome.trim())
                SecurityPreferences(myAdapter.context).storeString(Constants.KEY.ARRAY_TEL, arrayList.get(adapterPosition).telefone.trim())
                SecurityPreferences(myAdapter.context).storeInt(Constants.KEY.ARRAY_ADAPT, adapterPosition)
                startActivity(Intent(myAdapter.context, EmergencyDetailActivity::class.java))
            }
        }
        myAdapter.attListener(listener)

        EventChangeListener()
    }
    private fun EventChangeListener() {
        db.collection("DadosSocorristas").whereEqualTo("statusEncerrada", false)
            .addSnapshotListener{
            result, erro ->
            if(erro != null){
                Log.e("Firestore error", erro.message.toString())
                return@addSnapshotListener
            }
            binding.progressLogin.isVisible = false
            binding.textView3.isVisible = false
            for(doc : DocumentChange in result!!.getDocumentChanges()){
                if(doc.type == DocumentChange.Type.ADDED){
                    arrayList.add(doc.document.toObject(User().javaClass))
                    myAdapter.notifyDataSetChanged()
                }
            }
        }
    }
    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_voltar_register -> {
                startActivity(Intent(this, UserActivity::class.java))
                finish()
            }
        }
    }
}
