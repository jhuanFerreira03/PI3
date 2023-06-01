package br.edu.puc.sorriso24h.views

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivityCameraBinding
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() , View.OnClickListener{

    private lateinit var binding : ActivityCameraBinding

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector : CameraSelector
    private var imageCapture : ImageCapture?= null
    private lateinit var imgCaptureExecutor : ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        imgCaptureExecutor = Executors.newSingleThreadExecutor()

        binding.buttonTakePhoto.setOnClickListener(this)

        startCamera()
    }

    private fun startCamera(){
        cameraProviderFuture.addListener ({
            imageCapture = ImageCapture.Builder().build()

            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            }catch (e : Exception){
                Log.e("CameraPreview", "Falha ao abrir a camera!")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto () {
        imageCapture?.let {
            val fileName = "FOTO_JPEG_${System.currentTimeMillis()}"
            val file = File(externalMediaDirs[0], fileName)

            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
            it.takePicture(
                outputFileOptions,
                imgCaptureExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Log.i("CameraPreview", "A Imagem foi salva no diretorio: ${file.toURI()}")
                    }
                    override fun onError(exception: ImageCaptureException) {
                        Snackbar.make(binding.root, "Erro ao salvar a foto!", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.RED)
                            .show()
                        Log.e("CameraPreview", "Erro ao gravar a foto ${exception}")
                    }
                }
            )
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun blindPreviw(){
        binding.root.postDelayed({
            binding.root.foreground = ColorDrawable(Color.WHITE)
            binding.root.postDelayed({
                binding.root.foreground = null
            }, 50)
        }, 100)
    }
    override fun onClick(v: View){
        when(v.id) {
            R.id.button_takePhoto -> {
                takePhoto()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    blindPreviw()
                }
            }
        }
    }
}
