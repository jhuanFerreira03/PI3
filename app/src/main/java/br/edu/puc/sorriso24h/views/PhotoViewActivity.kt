package br.edu.puc.sorriso24h.views

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivityPhotoViewBinding
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
        binding.buttonBack.setColorFilter(ContextCompat.getColor(this, R.color.princ))

        setImage()
    }
    private fun setImage() {
        val file : File = File.createTempFile("tempfile", ".jpg")
        storage.getReference("images_user/${auth.currentUser!!.uid}").getFile(file).addOnSuccessListener {
            binding.imagePrinc.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            binding.imagePrinc.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
            binding.progressMain.visibility = View.INVISIBLE
        }
    }
    override fun onClick(v: View) {
        when(v.id){
            R.id.button_back -> {
                startActivity(Intent(this, AccountDetailsActivity::class.java))
                finish()
            }
        }
    }
}