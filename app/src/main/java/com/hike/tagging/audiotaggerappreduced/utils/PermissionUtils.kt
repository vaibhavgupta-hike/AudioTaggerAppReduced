package com.hike.tagging.audiotaggerappreduced.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.hike.tagging.audiotaggerappreduced.Constants

object PermissionUtils {

    fun checkAudioPermission(context: Context?, fragActivity: FragmentActivity): Boolean {
        val recordPermission = Manifest.permission.RECORD_AUDIO
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    recordPermission
                )
            } == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            ActivityCompat.requestPermissions(
                fragActivity,
                arrayOf(recordPermission),
                Constants.RECORD_AUDIO_PERMISSION_CODE
            )
            return false
        }
    }

    fun getStorageWritePermission(context: Context?, fragActivity: FragmentActivity): Boolean {
        val storageWritePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    storageWritePermission
                )
            } == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            ActivityCompat.requestPermissions(
                fragActivity,
                arrayOf(storageWritePermission),
                Constants.WRITE_EXT_STORAGE_PERMISSION_CODE
            )
            return false
        }
    }

    fun getStorageReadPermission(context: Context?, fragActivity: FragmentActivity): Boolean {
        val storageReadPermission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    storageReadPermission
                )
            } == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            ActivityCompat.requestPermissions(
                fragActivity,
                arrayOf(storageReadPermission),
                Constants.READ_EXT_STORAGE_PERMISSION_CODE
            )
            return false
        }
    }
}