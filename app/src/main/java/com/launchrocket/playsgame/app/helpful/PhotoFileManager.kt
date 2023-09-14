package com.launchrocket.playsgame.app.helpful

import android.content.Context
import android.os.Environment
import java.io.File

class PhotoFileManager {
    companion object {
        fun getTempFile(prefix: String, suffix: String, context: Context): File {
            return File.createTempFile(
                prefix,
                suffix,
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )
        }
    }
}