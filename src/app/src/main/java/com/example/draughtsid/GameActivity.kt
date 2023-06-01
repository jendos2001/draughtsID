package com.example.draughtsid

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
import com.example.draughtsid.databinding.ActivityGameBinding
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer


class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private var imageCapture: ImageCapture? = null
    private var orientation: List<String> = listOf("Vertical", "Horizontal")
    private var colorV = arrayListOf("White bottom", "Black bottom")
    private var colorH = arrayListOf("White Left", "Black Left")
    private var firstMove = arrayListOf("White", "Black")
    private lateinit var spinner: Spinner
    private lateinit var spinner2: Spinner
    private lateinit var spinner3: Spinner
    private lateinit var pyo: PyObject
    private var isInit = false
    private var isStart = false
    private var isCheckStartPosition = false
    private lateinit var startPosition: String
    private lateinit var curPosition: String
    private var game = ""
    private var isWhiteMove = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        spinner = binding.GameOrientation
        val adapter = ArrayAdapter(this@GameActivity, android.R.layout.simple_spinner_item, orientation)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner2 = binding.GameColor
        var adapter2 = ArrayAdapter(this@GameActivity, android.R.layout.simple_spinner_item, colorV)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner2.adapter = adapter2

        spinner3 = binding.firstMove
        var adapter3 = ArrayAdapter(this@GameActivity, android.R.layout.simple_spinner_item, firstMove)
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner3.adapter = adapter3
        setContentView(binding.root)

        binding.pos.setOnClickListener {
            val intent = Intent(this@GameActivity, MainActivity::class.java)
            startActivityForResult(intent, 2)
        }
        binding.user.setOnClickListener {
            val intent = Intent(this@GameActivity, ProfileActivity::class.java)
            startActivityForResult(intent, 4)
        }
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this@GameActivity))
        }
        if (!isInit) {
            Log.d("QQQ", "QQQQ")
            val py = Python.getInstance()
            pyo = py.getModule("detect")
            isInit = true
        }

        if(allPermissionsGranted()){
            startCamera()
        }
        else{
            ActivityCompat.requestPermissions(this, Constans.REQUIRED_PERMISSIONS, Constans.REQUEST_CODE_PERMISSION)
        }

        binding.button.setOnClickListener {
            if (!isCheckStartPosition){
                checkStart()
                isCheckStartPosition = true
                binding.button.text = "Start"
            }
            else if (!isStart){
                Log.d("START", "START")
                isStart = true
                binding.button.text = "Stop"
                takePhoto()
            }
            else{
                isStart = false
                Log.d("ASD", "ASDF")
                val intent = Intent(this@GameActivity, MetadataActivity::class.java)
                intent.putExtra("game", game)
                intent.putExtra("startPosition", startPosition)
                startActivityForResult(intent, 3)
            }
        }
        /*if (isStart && isCheckStartPosition){
            Log.d("After", "After")
            binding.button.text = "Stop"
            takePhoto()
        }*/

        val itemSelectedListener: AdapterView.OnItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {

                    if (parent.getItemAtPosition(position) == "Horizontal"){
                        adapter2 = ArrayAdapter(this@GameActivity, android.R.layout.simple_spinner_item, colorH)

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner2.adapter = adapter2
                    }
                    else {
                        adapter2 = ArrayAdapter(this@GameActivity, android.R.layout.simple_spinner_item, colorV)

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner2.adapter = adapter2
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        spinner.onItemSelectedListener = itemSelectedListener
    }

    private fun takePhoto(){
        val imageCapture = imageCapture ?: return
        while (isStart) {
            //Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({
            imageCapture.takePicture(
                ContextCompat.getMainExecutor(this),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        Log.d("VVV", "VVV")
                        val position = spinner.selectedItemPosition
                        val position2 = spinner2.selectedItemPosition
                        val position3 = spinner3.selectedItemPosition
                        if (position3 == 1) isWhiteMove = 1 - isWhiteMove
                        Log.d("Start22", "start22")

                        Log.d("Timer", "timer")
                        val bitmap = imageProxyToBitmap(image)
                        Log.d("Timer", "timer2")
                        Log.d("Timer", "timer3")
                        val imgString = getStringImage(bitmap)
                        val obj = pyo.callAttr("getMove", imgString, position, position2, isWhiteMove, curPosition)
                        val move = obj.toString()
                        val tmp = move.split("|")
                        if (tmp[1] != "") curPosition = tmp[1]
                        game += " ${tmp[0]}"
                        if (tmp[0] != "") isWhiteMove = 1 - isWhiteMove
                        Log.d("AAAAA", tmp[0])
                        Log.d("AAAAA", tmp[1])
                        image.close()


                        /*val intent = Intent(this@GameActivity, PositionActivity::class.java)
                        intent.putExtra("white", positions[0])
                        intent.putExtra("black", positions[1])
                        startActivityForResult(intent, 1)*/
                    }

                    override fun onError(exception: ImageCaptureException) {
                        super.onError(exception)
                    }

                    //})}, 0, 30, TimeUnit.SECONDS
                    //)
                })
        }
    }

    private fun checkStart(){
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val position = spinner.selectedItemPosition
                    val position2 = spinner2.selectedItemPosition
                    val bitmap = imageProxyToBitmap(image)
                    val imgString = getStringImage(bitmap)
                    val obj = pyo.callAttr("start", imgString, position, position2)
                    val pos = obj.toString().split("|")
                    if (pos[0] == "Не удалось найти доску"){
                        val intent = Intent(this@GameActivity, GameResutlActivity::class.java)
                        intent.putExtra("game", pos[0])
                        intent.putExtra("pdn", "")
                        startActivityForResult(intent, 5)
                    }
                    startPosition = pos[0]
                    curPosition = pos[0]
                    Log.d("AAAAA", pos[0])


                    /*val intent = Intent(this@GameActivity, PositionActivity::class.java)
                    intent.putExtra("white", positions[0])
                    intent.putExtra("black", positions[1])
                    startActivityForResult(intent, 1)*/
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