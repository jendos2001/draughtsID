package com.example.draughtsid

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Bundle
import android.util.Base64
import android.util.Log
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
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.draughtsid.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var outputDirectory: File

    private var imageCapture: ImageCapture? = null
    private var mOutputColumn = 10
    private var mThreshold = 0.95

    class Result(var classIndex: Int, var score: Float, rect: Rect) {
        var rect: Rect

        init {
            this.rect = rect
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        outputDirectory = getOutputDirectory()

        if(allPermissionsGranted()){
            startCamera()
        }
        else{
            ActivityCompat.requestPermissions(this, Constans.REQUIRED_PERMISSIONS, Constans.REQUEST_CODE_PERMISSION)
        }

        binding.button.setOnClickListener {
            takePhoto()
        }

    }


    private fun takePhoto(){
        val imageCapture = imageCapture ?: return
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(Constans.FILE_NAME_FORMAT, Locale.getDefault()).format(System.currentTimeMillis()) + ".jpg"
        )
        val outputOption = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        /*imageCapture.takePicture(
            outputOption, ContextCompat.getMainExecutor(this),
            object: ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo saved"
                    Toast.makeText(this@MainActivity, "$msg $savedUri", Toast.LENGTH_LONG).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(Constans.TAG, "onError: ${exception.message}", exception)
                }

            }
        )*/
        var testString: String = ""
        imageCapture.takePicture(ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                var bitmap = imageProxyToBitmap(image)

                /*val matrix = Matrix()
                matrix.postRotate(90.0f)
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true);
                val resizedBitmap = Bitmap.createScaledBitmap(
                    bitmap,
                    640,
                    640,
                    true
                )
                val module = Module.load(
                    assetFilePath(
                        application,
                        "best.torchscript.ptl"
                    )
                )
                val br = BufferedReader(InputStreamReader(assets.open("classes.txt")))
                var line: String
                val classes: MutableList<String> = ArrayList()
                line = br.readLine()
                while (line != null) {
                    classes.add(line)
                    line = br.readLine()
                }*/
                /*val classes: MutableList<String> = ArrayList()
                assets.open("classes.txt").bufferedReader().useLines {
                        lines->lines.forEach{
                        val x = it
                        classes.add (x)
                        }
                }

                val inputTensor: Tensor = TensorImageUtils.bitmapToFloat32Tensor(
                    resizedBitmap,
                    floatArrayOf(0.0f, 0.0f, 0.0f),
                    floatArrayOf(1.0f, 1.0f, 1.0f)
                )
                val outputTuple: Array<IValue> = module.forward(IValue.from(inputTensor)).toTuple()

                //обработка
                var board = 0
                var bd = 0
                var wd = 0
                val outputTensor = outputTuple[0].toTensor()
                val outputs = outputTensor.dataAsFloatArray
                val boards: ArrayList<Result> = ArrayList()
                val results: ArrayList<Result> = ArrayList()
                val imgScaleX = bitmap.width / 640
                val imgScaleY = bitmap.height / 640
                Log.d("AAAA", outputs.size.toString())
                for (i in 0 until 25200) {
                    if (outputs[i * mOutputColumn + 4] >= mThreshold) {
                        val x = outputs[i * mOutputColumn]
                        val y = outputs[i * mOutputColumn + 1]
                        val w = outputs[i * mOutputColumn + 2]
                        val h = outputs[i * mOutputColumn + 3]
                        val left: Float = imgScaleX * (x - w / 2)
                        val top: Float = imgScaleY * (y - h / 2)
                        val right: Float = imgScaleX * (x + w / 2)
                        val bottom: Float = imgScaleY * (y + h / 2)
                        var max = outputs[i * mOutputColumn + 5]
                        var cls = 0
                        for (j in 0 until mOutputColumn - 5) {
                            if (outputs[i * mOutputColumn + 5 + j] > max) {
                                max = outputs[i * mOutputColumn + 5 + j]
                                cls = j
                            }
                        }

                        val rect = Rect(
                            left.toInt(),
                            top.toInt(),
                            right.toInt(),
                            bottom.toInt()
                        )
                        if (cls == 0) board++
                        else if (cls == 1) bd++
                        else if (cls == 3) wd++
                        val result = Result(cls, outputs[i * mOutputColumn + 4], rect)
                        if (cls == 0) boards.add(result)
                        else results.add(result)
                    }
                }
                val boardList = nonMaxSuppression(results, 1, 0.5f)*/
                //Log.d("AAAA", "AAAA")
                //Log.d("AAAAA", maxScoreIdx.toString())
                //testString = tmp.toString()
                if (!Python.isStarted()) {
                    Python.start(AndroidPlatform(this@MainActivity))
                }

                val py = Python.getInstance()
                val imgString = getStringImage(bitmap)
                val pyo = py.getModule("detect")
                val obj = pyo.callAttr("start", imgString)
                val str = obj.toString()

                val intent = Intent(this@MainActivity, PositionActivity::class.java)
                intent.putExtra("test", "aaa")
                startActivityForResult(intent, 1)
                //super.onCaptureSuccess(image)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
            }

        })

    }

    private fun assetFilePath(context: Context, assetName: String): String? {
        val file = File(context.filesDir, assetName)

        try {
            context.assets.open(assetName).use { `is` ->
                FileOutputStream(file).use { os ->
                    val buffer = ByteArray(4 * 1024)
                    while (true) {
                        val length = `is`.read(buffer)
                        if (length <= 0)
                            break
                        os.write(buffer, 0, length)
                    }
                    os.flush()
                    os.close()
                }
                Log.d("pytorchandroid", file.absolutePath)
                return file.absolutePath
            }
        } catch (e: IOException) {
            Log.e("pytorchandroid", "Error process asset $assetName to file path")
        }

        return null
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val planeProxy = image.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun getOutputDirectory(): File{
        val mediaDir = externalMediaDirs.firstOrNull()?.let {mFile->
            File(mFile, resources.getString(R.string.app_name)).apply {
                mkdirs()

            }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir
        else filesDir
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

    fun nonMaxSuppression(
        boxes: ArrayList<Result>,
        limit: Int,
        threshold: Float
    ): ArrayList<Result>? {

        // Do an argsort on the confidence scores, from high to low.
        boxes.sortWith(Comparator { o1, o2 -> o1!!.score.compareTo(o2!!.score) })
        val selected: ArrayList<Result> = ArrayList()
        val active = BooleanArray(boxes.size)
        Arrays.fill(active, true)
        var numActive = active.size

        // The algorithm is simple: Start with the box that has the highest score.
        // Remove any remaining boxes that overlap it more than the given threshold
        // amount. If there are any boxes left (i.e. these did not overlap with any
        // previous boxes), then repeat this procedure, until no more boxes remain
        // or the limit has been reached.
        var done = false
        var i = 0
        while (i < boxes.size && !done) {
            if (active[i]) {
                val boxA = boxes[i]
                selected.add(boxA)
                if (selected.size >= limit) break
                for (j in i + 1 until boxes.size) {
                    if (active[j]) {
                        val boxB = boxes[j]
                        if (IOU(boxA.rect, boxB.rect) > threshold) {
                            active[j] = false
                            numActive -= 1
                            if (numActive <= 0) {
                                done = true
                                break
                            }
                        }
                    }
                }
            }
            i++
        }
        return selected
    }

    /**
     * Computes intersection-over-union overlap between two bounding boxes.
     */
    fun IOU(a: Rect, b: Rect): Float {
        val areaA = ((a.right - a.left) * (a.bottom - a.top)).toFloat()
        if (areaA <= 0.0) return 0.0f
        val areaB = ((b.right - b.left) * (b.bottom - b.top)).toFloat()
        if (areaB <= 0.0) return 0.0f
        val intersectionMinX = a.left.coerceAtLeast(b.left).toFloat()
        val intersectionMinY = a.top.coerceAtLeast(b.top).toFloat()
        val intersectionMaxX = a.right.coerceAtMost(b.right).toFloat()
        val intersectionMaxY = a.bottom.coerceAtMost(b.bottom).toFloat()
        val intersectionArea = (intersectionMaxY - intersectionMinY).coerceAtLeast(0f) *
                (intersectionMaxX - intersectionMinX).coerceAtLeast(0f)
        return intersectionArea / (areaA + areaB - intersectionArea)
    }

    private fun getStringImage(bitmap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val imageBytes: ByteArray = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }


}