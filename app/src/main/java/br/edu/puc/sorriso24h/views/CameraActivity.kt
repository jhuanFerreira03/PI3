package br.edu.puc.sorriso24h.views

import android.content.Intent
import android.graphics.Camera
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
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
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import io.grpc.Context.Storage
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
    private lateinit var storage : FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        if (SecurityPreferences(this).getString("photo") == "front") {
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        }
        else if(SecurityPreferences(this).getString("photo") == "back"){
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
        imgCaptureExecutor = Executors.newSingleThreadExecutor()
        storage = Firebase.storage

        binding.buttonTakePhoto.setOnClickListener(this)
        binding.buttonSwitchCamera.setOnClickListener(this)

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
                        val filearq = Uri.fromFile(file)
                        SecurityPreferences(binding.root.context).storeString("ft_perfil", filearq.toString())
                        startActivity(Intent(binding.root.context, PhotoRegisterActivity::class.java))
                        finish()
                        /*val filearq = Uri.fromFile(file)
                        val riversRef = storage.reference.child("images/${fileName}")
                        val uploadTask = riversRef.putFile(filearq)
                        uploadTask.addOnFailureListener {
                            Snackbar.make(binding.root, "Imagem enviada!", Snackbar.LENGTH_LONG).setBackgroundTint(Color.GREEN).show()
                        }.addOnSuccessListener { taskSnapshot ->
                            Snackbar.make(binding.root, "Imagem não enviada!", Snackbar.LENGTH_LONG).setBackgroundTint(Color.RED).show()
                        }*/
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
    private fun switchCamera(){
        if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA){
            SecurityPreferences(this).storeString("photo", "front")
            recreate()
        }
        else{
            SecurityPreferences(this).storeString("photo", "back")
            recreate()
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
            R.id.button_switchCamera -> switchCamera()
        }
    }
}
