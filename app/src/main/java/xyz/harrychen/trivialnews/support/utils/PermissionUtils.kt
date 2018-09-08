package xyz.harrychen.trivialnews.support.utils

import android.Manifest
import android.app.Activity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class PermissionUtils {
    companion object {
        var PERMISSION_STORAGE = false

        fun askForPermission(activity: Activity) {
            Dexter.withActivity(activity)
                    .withPermissions(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    ).withListener(object:MultiplePermissionsListener{
                        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                            PERMISSION_STORAGE = report!!.areAllPermissionsGranted()
                        }

                        override fun onPermissionRationaleShouldBeShown(
                                permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {

                        }
                    })
                    .check()
        }
    }
}