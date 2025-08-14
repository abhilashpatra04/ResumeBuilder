package com.example.resumebuilder.utils

import android.content.Context

object PdfExport {
    fun exportA4Pdf(context: Context): Result<String> {
        // TODO: Implement PdfDocument rendering of the Compose template
        return Result.success("file:///storage/emulated/0/Download/resume.pdf")
    }
}
