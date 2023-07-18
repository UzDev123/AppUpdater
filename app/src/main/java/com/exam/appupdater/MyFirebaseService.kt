package com.exam.appupdater

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val pathReference = storageRef.child("New Updater.apk")



        val ONE_MEGABYTE: Long = 8 * 1024 * 1024
        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener {
            writeFileTo(it)
        }
    }



    private fun writeFileTo(bytes: ByteArray?) {
        val file = File(Environment.getExternalStorageDirectory(), "NewApp.apk")

        if (!file.exists()) {
            file.parentFile?.mkdirs()
        }
        Log.d("mlog", "filePath: ${file.path}");

        try {
            val fos: OutputStream = FileOutputStream(file)
            fos.write(bytes)
            fos.close()
            Log.d("mlog", "is wrote: ");
            installAPK()
        } catch (e: java.lang.Exception) {
            Log.d("mlog", "Error: ${e.message}");
        }


    }

    fun checkApkFile(): Boolean {
        val file = File(Environment.getExternalStorageDirectory(), "NewApp.apk")
        return file.exists()

    }


    private fun installAPK() {
        val file = File(Environment.getExternalStorageDirectory(), "NewApp.apk")
        if (file.exists()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(
                uriFromFile(
                    applicationContext,
                    File(Environment.getExternalStorageDirectory(), "NewApp.apk")
                ),
                "application/vnd.android.package-archive"
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                applicationContext.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                Log.e("TAG", "Error in opening the file!")
            }
        } else {
            Toast.makeText(applicationContext, "installing", Toast.LENGTH_LONG).show()
        }
    }

    private fun uriFromFile(context: Context, file: File?): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context, BuildConfig.APPLICATION_ID + ".provider",
                file!!
            )
        } else {
            Uri.fromFile(file)
        }
    }


}