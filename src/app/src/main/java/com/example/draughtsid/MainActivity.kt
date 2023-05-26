package com.example.draughtsid

import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.draughtsid.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var imageCapture: ImageCapture? = null
    private var orientation: List<String> = listOf("Vertical", "Horizontal")
    private var colorV = arrayListOf("White bottom", "Black bottom")
    private var colorH = arrayListOf("White Left", "Black Left")
    private lateinit var spinner: Spinner
    private lateinit var spinner2: Spinner
    private lateinit var pyo: PyObject
    private var isInit = false


    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this@MainActivity))
        }
        if (!isInit) {
            Log.d("GGGG", "GGGG")
            val py = Python.getInstance()
            pyo = py.getModule("detect")
            isInit = true
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        spinner = binding.orientation
        val adapter = ArrayAdapter(this@MainActivity, R.layout.simple_spinner_item, orientation)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner2 = binding.color
        var adapter2 = ArrayAdapter(this@MainActivity, R.layout.simple_spinner_item, colorV)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner2.adapter = adapter2
        setContentView(binding.root)
        binding.game.setOnClickListener{
            val intent = Intent(this@MainActivity, GameActivity::class.java)
            startActivityForResult(intent, 2)
        }
        binding.user.setOnClickListener{
            val intent = Intent(this@MainActivity, ProfileActivity::class.java)
            startActivityForResult(intent, 4)
        }


        if(allPermissionsGranted()){
            startCamera()
        }
        else{
            ActivityCompat.requestPermissions(this, Constans.REQUIRED_PERMISSIONS, Constans.REQUEST_CODE_PERMISSION)
        }

        binding.button.setOnClickListener {
            takePhoto()
        }

        val itemSelectedListener: AdapterView.OnItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {

                    if (parent.getItemAtPosition(position) == "Horizontal"){
                        adapter2 = ArrayAdapter(this@MainActivity, R.layout.simple_spinner_item, colorH)

                        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner2.adapter = adapter2
                    }
                    else {
                        adapter2 = ArrayAdapter(this@MainActivity, R.layout.simple_spinner_item, colorV)

                        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner2.adapter = adapter2
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        spinner.onItemSelectedListener = itemSelectedListener

    }


    private fun takePhoto(){
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val bitmap = imageProxyToBitmap(image)
                image.close()
                val position = spinner.selectedItemPosition
                val position2 = spinner2.selectedItemPosition

                val imgString = getStringImage(bitmap)
                val obj = pyo.callAttr("start", imgString, position, position2)
                val positions = obj.toString().split("|")
                val intent = Intent(this@MainActivity, PositionActivity::class.java)
                Log.d("AAA", positions[0])
                if (positions[0] == "Не удалось распознать позицию"){
                    Log.d("BBB", "BBB")
                    intent.putExtra("success", false)
                    intent.putExtra("white", positions[0])
                }
                else {
                    intent.putExtra("success", true)
                    intent.putExtra("white", positions[0])
                    intent.putExtra("black", positions[1])
                }
                startActivityForResult(intent, 1)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
            }

        })

    }


    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val planeProxy = image.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }


    private fun startCamera(){
        val camerProviderFuture = ProcessCameraProvider.getInstance(this)

        camerProviderFuture.addListener({
            val camerProvider: ProcessCameraProvider = camerProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    mPreview -> mPreview.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                camerProvider.unbindAll()
                camerProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            }
            catch (e: Exception){
                Log.d(Constans.TAG, "startCameraFail: ", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constans.REQUEST_CODE_PERMISSION){

            if (allPermissionsGranted()){
                startCamera()
            }
            else{
                Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show()
                finish()
            }

        }
    }



    private fun allPermissionsGranted() =
        Constans.REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
        }

    private fun getStringImage(bitmap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val imageBytes: ByteArray = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }


}