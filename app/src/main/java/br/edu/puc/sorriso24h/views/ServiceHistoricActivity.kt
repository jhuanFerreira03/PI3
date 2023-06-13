package br.edu.puc.sorriso24h.views

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.puc.sorriso24h.Adapter.MyAdapter
import br.edu.puc.sorriso24h.Adapter.User
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivityServiceHistoricBinding
import br.edu.puc.sorriso24h.infra.Constants
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import br.edu.puc.sorriso24h.listener.ListListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class ServiceHistoricActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityServiceHistoricBinding

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var messaging : FirebaseMessaging

    private lateinit var recyclerView : RecyclerView
    private lateinit var arrayList : ArrayList<User>
    private lateinit var myAdapter : MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceHistoricBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.imageArrowBack.setColorFilter(ContextCompat.getColor(this, R.color.second))
        binding.btnVoltarRegister.setOnClickListener(this)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        messaging = FirebaseMessaging.getInstance()

        recyclerView = findViewById(R.id.recycle)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        arrayList = arrayListOf()

        myAdapter = MyAdapter(this, arrayList)

        recyclerView.adapter = myAdapter

        val listener = object : ListListener {
            override fun onClick(adapterPosition: Int) {
                SecurityPreferences(myAdapter.context).storeString(Constants.KEY_SHARED.ARRAY_NAME, arrayList[adapterPosition].nome.trim())
                SecurityPreferences(myAdapter.context).storeString(Constants.KEY_SHARED.ARRAY_TEL, arrayList[adapterPosition].telefone.trim())
                SecurityPreferences(myAdapter.context).storeString("id", arrayList[adapterPosition].id.trim())
                SecurityPreferences(myAdapter.context).storeInt(Constants.KEY_SHARED.ARRAY_ADAPT, adapterPosition)
                startActivity(Intent(myAdapter.context, EmergencyDetailActivity::class.java))
            }
        }
        myAdapter.attListener(listener)

        eventChangeListener()

    }
    @SuppressLint("NotifyDataSetChanged")
    private fun eventChangeListener() {
        db.collection(Constants.DB.ATENDIMENTOS)
            .whereEqualTo("status", "fechado").addSnapshotListener { result, e ->
                for (doc : DocumentChange in result!!.documentChanges) {
                    if (doc.type == DocumentChange.Type.ADDED) {
                        arrayList.add(doc.document.toObject(User().javaClass))
                        arrayList[arrayList.count() - 1].id = doc.document["emergencia"].toString()
                    }
                    myAdapter.notifyDataSetChanged()
                }
            }
    }
    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_voltar_register -> {
                startActivity(Intent(myAdapter.context, UserActivity::class.java))
                finish()
            }
        }
    }
}