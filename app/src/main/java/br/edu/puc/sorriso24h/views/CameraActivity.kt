package br.edu.puc.sorriso24h.views

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Camera
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import br.edu.puc.sorriso24h.R
import br.edu.puc.sorriso24h.databinding.ActivityCameraBinding
import br.edu.puc.sorriso24h.infra.Constants
import br.edu.puc.sorriso24h.infra.SecurityPreferences
import com.google.android.gms.common.moduleinstall.ModuleInstallStatusUpdate.ProgressInfo
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import io.grpc.Context.Storage
import kotlinx.coroutines.tasks.await
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
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        imgCaptureExecutor = Executors.newSingleThreadExecutor()
        setCam()
        storage = Firebase.storage
        auth = FirebaseAuth.getInstance()

        binding.buttonTakePhoto.setOnClickListener(this)
        binding.buttonSwitchCameraBack.setOnClickListener(this)
        binding.buttonSwitchCameraFront.setOnClickListener(this)

        binding.imageCancel.setOnClickListener(this)
        binding.imageConfirm.setOnClickListener(this)

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
                        if (SecurityPreferences(applicationContext).getString("deciderPicture").toString() == "detail"){
                            val bm = MediaStore.Images.Media.getBitmap(contentResolver, SecurityPreferences(binding.root.context).getString("ft_perfil")!!.toUri())
                            this@CameraActivity.runOnUiThread(java.lang.Runnable {
                                binding.imagePop.setImageBitmap(bm)
                                binding.cameraPreview.visibility = View.INVISIBLE
                                binding.buttonTakePhoto.visibility = View.INVISIBLE
                                binding.buttonSwitchCameraBack.visibility = View.INVISIBLE
                                binding.buttonSwitchCameraFront.visibility = View.INVISIBLE

                                binding.imagePop.visibility = View.VISIBLE
                                binding.imageCancel.visibility = View.VISIBLE
                                binding.imageConfirm.visibility = View.VISIBLE
                            })
                        }
                        else if(SecurityPreferences(binding.root.context).getString("deciderPicture").toString() == "register") {
                            startActivity(Intent(binding.root.context, PhotoRegisterActivity::class.java))
                            finish()
                        }
                        Log.i("CameraPreview", "A Imagem foi salva no diretorio: ${file.toURI()}")
                    }
                    override fun onError(exception: ImageCaptureException) {
                        Snackbar.make(binding.root, "Erro ao salvar a foto!", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.RED)
                            .show()
                        Log.e("CameraPreview", "Erro ao gravar a foto $exception")
                    }
                }
            )
        }
    }
    private fun uploadImage(){
        binding.textUpload.visibility = View.VISIBLE
        binding.progressCamera.visibility = View.VISIBLE
        storage.reference.child("images_user/${auth.currentUser?.uid}").delete()
        val riversRef = storage.reference.child("images_user/${auth.currentUser?.uid}")
        val uploadTask = riversRef.putFile(SecurityPreferences(applicationContext).getString("ft_perfil")!!.toUri())
            .addOnCompleteListener{
                SecurityPreferences(applicationContext).storeString("ft_perfil", "")
                startActivity(Intent(binding.root.context, AccountDetailsActivity::class.java))
                finish()
            }
    }
    private fun switchCamera(dec : String){
        if(dec == Constants.CAMERA.FRONT){
            SecurityPreferences(this).storeString(Constants.KEY_SHARED.PHOTO, dec)
            recreate()
        }
        else if (dec == Constants.CAMERA.BACK){
            SecurityPreferences(this).storeString(Constants.KEY_SHARED.PHOTO, dec)
            recreate()
        }
    }
    private fun setCam(){
        if (SecurityPreferences(this).getString(Constants.KEY_SHARED.PHOTO).toString() == Constants.CAMERA.FRONT) {
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            binding.buttonSwitchCameraBack.visibility = View.VISIBLE
            binding.buttonSwitchCameraFront.visibility = View.INVISIBLE
        }
        else if(SecurityPreferences(this).getString(Constants.KEY_SHARED.PHOTO).toString() == Constants.CAMERA.BACK){
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            binding.buttonSwitchCameraFront.visibility = View.VISIBLE
            binding.buttonSwitchCameraBack.visibility = View.INVISIBLE
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
            R.id.button_switchCamera_back -> switchCamera(Constants.CAMERA.BACK)
            R.id.button_switchCamera_front -> switchCamera(Constants.CAMERA.FRONT)
            R.id.image_confirm -> uploadImage()
            R.id.image_cancel -> {
                binding.imagePop.visibility = View.INVISIBLE
                binding.imageCancel.visibility = View.INVISIBLE
                binding.imageConfirm.visibility = View.INVISIBLE
                binding.buttonTakePhoto.visibility = View.VISIBLE
                binding.buttonSwitchCameraBack.visibility = View.VISIBLE
                binding.buttonSwitchCameraFront.visibility = View.VISIBLE
                binding.cameraPreview.visibility =View.VISIBLE
            }
        }
    }
}
