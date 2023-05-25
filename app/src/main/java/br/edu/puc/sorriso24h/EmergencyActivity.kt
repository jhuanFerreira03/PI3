package br.edu.puc.sorriso24h

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.puc.sorriso24h.Adapter.ListAdapter
import br.edu.puc.sorriso24h.Adapter.MyAdapter
import br.edu.puc.sorriso24h.Adapter.User
import br.edu.puc.sorriso24h.databinding.ActivityEmergencyBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.reflect.typeOf

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

        sla()

        supportActionBar?.hide()

        binding.imageArrowBack.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
        binding.btnVoltarRegister.setOnClickListener(this)

        recyclerView = findViewById(R.id.recycle)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        arrayList = arrayListOf()

        myAdapter = MyAdapter(this, arrayList)

        recyclerView.adapter = myAdapter

        EventChangeListener()

        //binding.recyclerEmergencyList.layoutManager = LinearLayoutManager(this)

        //binding.recyclerEmergencyList.adapter = ListAdapter()
    }

    private fun EventChangeListener() {
        db.collection("DadosSocorristas").addSnapshotListener{
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

    private fun sla(){
        db.collection("DadosSocorristas").document("DzVyhYJS2oRg0PSZXpMT").addSnapshotListener{it, e ->
            if (it != null) {
                binding.textTest.setText(it.get("nome").toString() + "\n" + it.get("telefone").toString())
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
