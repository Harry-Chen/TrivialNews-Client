package xyz.harrychen.trivialnews.support.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel


class QrCodeUtils {
    companion object {

        fun generateQrCodeFromString(str: String, width: Int = 400): Bitmap {

            val bitMatrix = MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, width, width,
                    mapOf(EncodeHintType.MARGIN to 1,
                            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.Q))
            val bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)
            for (i in 0 until width) {
                for (j in 0 until width) {
                    bitmap.setPixel(i, j,
                            if (bitMatrix.get(i, j))
                                Color.BLACK
                            else
                                Color.WHITE)
                }
            }
            return bitmap

        }

    }
}