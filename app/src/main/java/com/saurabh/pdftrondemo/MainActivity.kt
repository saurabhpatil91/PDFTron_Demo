package com.saurabh.pdftrondemo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.pdftron.pdf.config.ViewerConfig
import com.pdftron.pdf.controls.DocumentActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val STORAGE_REQUEST_CODE = 101
    private val RECORD_REQUEST_CODE = 102
    private val PDF_SELECTION_CODE = 103

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupPermissions()

        val config = ViewerConfig.Builder().openUrlCachePath(this.getCacheDir().getAbsolutePath()).build();


        btnURL.setOnClickListener(){//
            val fileLink = Uri.parse("https://pdftron.s3.amazonaws.com/downloads/pl/PDFTRON_mobile_about.pdf")
            DocumentActivity.openDocument(this, fileLink, config)
        }

        btnAsset.setOnClickListener(){
            DocumentActivity.openDocument(this, R.raw.sample_file, config)
        }

        btnStorage.setOnClickListener(){
            browsePDF()
        }

        /*

        // Set the cache location using the config to store the cache file
        val config = ViewerConfig.Builder().openUrlCachePath(this.getCacheDir().getAbsolutePath()).build();

        // from internal storage

        val localFile = Uri.fromFile(new File("myLocalFilePath"))
        DocumentActivity.openDocument(context, localFile)
        // or if using config
        DocumentActivity.openDocument(context, localFile, config)
        // or if using config and opening password protected file
        DocumentActivity.openDocument(context, localFile, password, config)

        // from content uri
        val contentUri = Uri.parse("myContentUri")
        DocumentActivity.openDocument(context, contentUri)
        // or if using config
        DocumentActivity.openDocument(context, contentUri, config)
        // or if using config and opening password protected file
        DocumentActivity.openDocument(context, contentUri, password, config)

        // from http/https
        val fileLink = Uri.parse("myFileLink")
        DocumentActivity.openDocument(context, fileLink)
        // or if using config
        DocumentActivity.openDocument(context, fileLink, config)
        // or if using config and opening password protected file
        DocumentActivity.openDocument(context, fileLink, password, config)

        // from res (the file must be placed in the `res/raw folder`)
        DocumentActivity.openDocument(context, R.raw.my_file_res_id)
        // or if using config
        DocumentActivity.openDocument(context, R.raw.my_file_res_id, config)
        // or if using config and opening password protected file
        DocumentActivity.openDocument(context, R.raw.my_file_res_id, password, config)*/
    }

    private fun browsePDF(){
        val browseStorage = Intent(Intent.ACTION_GET_CONTENT)
        browseStorage.type = "application/pdf"
        browseStorage.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(
            Intent.createChooser(browseStorage, "Select PDF"), PDF_SELECTION_CODE
        )
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Permission to access the write storage is required for this app to download pdf file")
                        .setTitle("Permission required")

                            builder.setPositiveButton("OK"
                            ) { dialog, id ->
                        Log.i(TAG, "Clicked")
                        makeRequest()
                    }

                    val dialog = builder.create()
                dialog.show()
            } else {
                makeRequest()
            }
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            STORAGE_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PDF_SELECTION_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedPdfFromStorage = data.data
            DocumentActivity.openDocument(this, selectedPdfFromStorage)

        }
    }
}