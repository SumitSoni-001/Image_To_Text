package com.example.ocrapp

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.SparseArray
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import org.jetbrains.annotations.Contract
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var showText: TextView
    private lateinit var btnCapture: Button
    private lateinit var btnCopy: Button
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showText = findViewById(R.id.showText)
        btnCopy = findViewById(R.id.btnCopy)
        btnCapture = findViewById(R.id.btnCapture)

        btnCapture.setOnClickListener {
            checkPermission()

//            ImagePicker.with(this)
//                .crop()
//                .cameraOnly()
//                .compress(1024)
//                .maxResultSize(1080, 1080).start()

        }

        btnCopy.setOnClickListener {
            /** Copying to Clipboard */
            val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(ClipData.newPlainText("textFromImage", showText.text))
            Toast.makeText(this, "Copied To Clipboard", Toast.LENGTH_SHORT).show()
        }

    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                100
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            getTextFromImage(bitmap)

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getTextFromImage(image: Bitmap) {
//        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val recognizer = TextRecognizer.Builder(this).build()

        if (!recognizer.isOperational) {
            Toast.makeText(this, "Error Occurred!", Toast.LENGTH_SHORT).show()
        } else {
            val frame = Frame.Builder().setBitmap(image).build()
            val sparseArray: SparseArray<TextBlock> = recognizer.detect(frame)
            val stringBuilder = StringBuilder()

            for (i in 0 until (sparseArray.size())) {
                val textBlock = sparseArray.valueAt(i)
                stringBuilder.append(textBlock.value)
                stringBuilder.append("\n")
            }
            showText.text = stringBuilder.toString()
            btnCapture.text = "Retake"
            btnCopy.visibility = View.VISIBLE

        }

    }

}