package br.edu.puc.sorriso24h.views

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivityPhotoViewBinding
import br.edu.puc.sorriso24h.infra.Constants
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class PhotoViewActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityPhotoViewBinding

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var messaging : FirebaseMessaging
    private lateinit var storage : FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        messaging = FirebaseMessaging.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.buttonBack.setOnClickListener(this)
        binding.progressMain.visibility = View.VISIBLE

        setImage()
    }
    private fun setImage() {
        val file : File = File.createTempFile("tempfile", ".jpg")
        storage.getReference(SecurityPreferences(this).getString(Constants.KEY_SHARED.PHOTO_VIEW).toString()).getFile(file).addOnSuccessListener {
            binding.imagePrinc.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            binding.imagePrinc.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
            binding.progressMain.visibility = View.INVISIBLE
        }
    }
    override fun onClick(v: View) {
        when(v.id){
            R.id.button_back -> {
                if (SecurityPreferences(this).getString(Constants.KEY_SHARED.PHOTO_VIEW_DECIDER) == "account") {
                    startActivity(Intent(this, AccountDetailsActivity::class.java))
                }
                else if(SecurityPreferences(this).getString(Constants.KEY_SHARED.PHOTO_VIEW_DECIDER) == "emergencyDetail") {
                    startActivity(Intent(this, EmergencyDetailActivity::class.java))
                }
                finish()
            }
        }
    }
}