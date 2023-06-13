package br.edu.puc.sorriso24h.views

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaScannerConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Video.Media
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
import br.edu.puc.sorriso24h.infra.Constants
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.google.rpc.context.AttributeContext.Resource
import io.grpc.internal.SharedResourceHolder
import java.io.File
import java.util.concurrent.ExecutorService

class PhotoRegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityPhotoRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPhotoRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.imageArrowBack.setColorFilter(ContextCompat.getColor(this, R.color.second))
        binding.imageArrowNext.setColorFilter(ContextCompat.getColor(this, R.color.second))

        SecurityPreferences(this).storeString(Constants.KEY_SHARED.PHOTO, Constants.CAMERA.FRONT)

        binding.buttonOpenCam.setOnClickListener(this)
        binding.btnAvancarRegister.setOnClickListener(this)
        binding.btnVoltarRegister.setOnClickListener(this)

        setPhoto()
    }
    private fun setPhoto() {
        if(SecurityPreferences(this).getString(Constants.KEY_SHARED.FT_PERFIL).toString() != "") {
            //binding.imagePhotoPerfil.setImageURI(SecurityPreferences(this).getString("ft_string")!!.toUri())
            val bm = MediaStore.Images.Media.getBitmap(contentResolver, SecurityPreferences(this).getString(Constants.KEY_SHARED.FT_PERFIL)!!.toUri())
            binding.imagePhotoMini.setImageBitmap(bm)
            binding.imagePhotoMini.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
        }
    }
    private val cameraProviderResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if(it) {
            startActivity(Intent(this, CameraActivity::class.java))
        } else {
            Snackbar.make(binding.root, "É necessário conceder permissão de camera!", Toast.LENGTH_LONG)
                .setBackgroundTint(Color.rgb(229,0,37))
                .show()
        }
    }
    override fun onClick(v: View) {
        when(v.id) {
            R.id.button_openCam -> {
                SecurityPreferences(applicationContext).storeString("deciderPicture", "register")
                cameraProviderResult.launch(android.Manifest.permission.CAMERA)
            }
            R.id.btn_voltar_register -> {
                startActivity(Intent(this, Register1Activity::class.java))
                finish()
            }
            R.id.btn_avancar_register -> {
                if (SecurityPreferences(this).getString(Constants.KEY_SHARED.FT_PERFIL).toString() == "") {
                    Snackbar.make(binding.root, "Tire uma foto!", Snackbar.LENGTH_LONG).setBackgroundTint(Color.rgb(229,0,37)).show()
                    return
                }
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
    }
}