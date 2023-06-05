package br.edu.puc.sorriso24h.views

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivityPhotoRegisterBinding
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.google.rpc.context.AttributeContext.Resource
import io.grpc.internal.SharedResourceHolder
import java.util.concurrent.ExecutorService

class PhotoRegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityPhotoRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPhotoRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.imageArrowBack.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
        binding.imageArrowNext.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))

        binding.buttonOpenCam.setOnClickListener(this)
        binding.btnAvancarRegister.setOnClickListener(this)
        binding.btnVoltarRegister.setOnClickListener(this)

        setPhoto()
    }

    private fun setPhoto() {
        if(SecurityPreferences(this).getString("ft_perfil").toString() != ""){
            binding.imagePhotoPerfil.setImageURI(SecurityPreferences(this).getString("ft_string")?.toUri())
        }
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
            R.id.btn_voltar_register -> {
                startActivity(Intent(this, MilagreActivity::class.java))
                finish()
            }
            R.id.btn_avancar_register -> {
                if (SecurityPreferences(this).getString("ft_perfil").toString() == ""){
                    Snackbar.make(binding.root, "Tire uma foto!", Snackbar.LENGTH_LONG).setBackgroundTint(Color.RED).show()
                    return
                }
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
    }
}