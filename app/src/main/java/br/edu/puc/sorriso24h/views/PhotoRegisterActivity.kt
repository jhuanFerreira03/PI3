package br.edu.puc.sorriso24h.views

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivityPhotoRegisterBinding
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService

class PhotoRegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityPhotoRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPhotoRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonOpenCam.setOnClickListener(this)

    }
    private val cameraProviderResult = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if(it){
            startActivity(Intent(this, CameraActivity::class.java))
        } else {
            Snackbar.make(binding.root, "É necessário conceder permissão de camera!", Toast.LENGTH_LONG)
                .setBackgroundTint(Color.RED)
                .show()
        }
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.button_openCam -> {
                cameraProviderResult.launch(android.Manifest.permission.CAMERA)
            }
        }
    }
}