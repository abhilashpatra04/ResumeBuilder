package com.example.resumebuilder.utils

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import com.example.resumebuilder.data.ResumeData
import com.example.resumebuilder.data.Template
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExport {
    private const val TAG = "PdfExport"
    
    /**
     * Exports the resume data to a PDF file in the Downloads directory
     * 
     * @param context The application context
     * @param resumeData The resume data to export
     * @return Result containing the file URI if successful, or an error message if failed
     */
    fun exportA4Pdf(context: Context, resumeData: ResumeData): Result<String> {
        try {
            // Create a PDF document with A4 dimensions
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 in points (72 dpi)
            val page = pdfDocument.startPage(pageInfo)
            
            val canvas = page.canvas
            
            // Draw content based on template
            when (resumeData.selectedTemplate) {
                Template.MODERN -> drawModernTemplate(canvas, resumeData)
                Template.PROFESSIONAL -> drawProfessionalTemplate(canvas, resumeData)
                else -> drawModernTemplate(canvas, resumeData) // Default to Modern
            }
            
            pdfDocument.finishPage(page)
            
            // Create file in Downloads directory
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val fileName = "Resume_${timestamp}.pdf"
            val file = File(downloadsDir, fileName)
            
            // Write to file
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            
            pdfDocument.close()
            
            return Result.success("file://${file.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting PDF", e)
            return Result.failure(e)
        }
    }
    
    // For Phase 1, we'll implement simple text-based templates
    // In Phase 2, we would implement more sophisticated rendering
    
    private fun drawModernTemplate(canvas: android.graphics.Canvas, resumeData: ResumeData) {
        // Basic implementation for Phase 1
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 12f
        }
        
        var y = 50f
        
        // Header
        paint.textSize = 24f
        canvas.drawText(resumeData.personalInfo.fullName ?: "Your Name", 50f, y, paint)
        
        y += 30f
        paint.textSize = 16f
        canvas.drawText(resumeData.personalInfo.jobTitle ?: "Your Job Title", 50f, y, paint)
        
        // Contact info
        y += 30f
        paint.textSize = 12f
        resumeData.personalInfo.email?.let {
            canvas.drawText(it, 50f, y, paint)
        }
        
        y += 20f
        resumeData.personalInfo.phone?.let {
            canvas.drawText(it, 50f, y, paint)
        }
        
        // Summary
        y += 40f
        paint.textSize = 14f
        canvas.drawText("SUMMARY", 50f, y, paint)
        
        y += 20f
        paint.textSize = 12f
        resumeData.personalInfo.summary?.let {
            canvas.drawText(it, 50f, y, paint)
        }
        
        // Experience
        if (resumeData.experiences.isNotEmpty()) {
            y += 40f
            paint.textSize = 14f
            canvas.drawText("EXPERIENCE", 50f, y, paint)
            
            resumeData.experiences.forEach { exp ->
                y += 30f
                paint.textSize = 12f
                paint.isFakeBoldText = true
                canvas.drawText(exp.jobTitle ?: "", 50f, y, paint)
                
                y += 20f
                paint.isFakeBoldText = false
                canvas.drawText("${exp.company ?: ""} | ${exp.startDate ?: ""} - ${exp.endDate ?: "Present"}", 50f, y, paint)
                
                exp.bulletPoints?.forEach { bullet ->
                    y += 20f
                    canvas.drawText("• $bullet", 70f, y, paint)
                }
            }
        }
        
        // Skills
        if (resumeData.skills.isNotEmpty()) {
            y += 40f
            paint.textSize = 14f
            canvas.drawText("SKILLS", 50f, y, paint)
            
            y += 20f
            paint.textSize = 12f
            canvas.drawText(resumeData.skills.joinToString(", ") { it.name ?: "" }, 50f, y, paint)
        }
    }
    
    private fun drawProfessionalTemplate(canvas: android.graphics.Canvas, resumeData: ResumeData) {
        // Basic implementation for Phase 1
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 12f
        }
        
        // Draw sidebar background
        val sidebarPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(240, 240, 245) // Light blush color
        }
        canvas.drawRect(0f, 0f, 150f, 842f, sidebarPaint)
        
        // Sidebar content
        var y = 100f
        paint.textSize = 14f
        paint.color = android.graphics.Color.rgb(80, 80, 100) // Darker color for sidebar text
        canvas.drawText("CONTACT", 30f, y, paint)
        
        y += 30f
        paint.textSize = 12f
        resumeData.personalInfo.email?.let {
            canvas.drawText(it, 30f, y, paint)
        }
        
        y += 20f
        resumeData.personalInfo.phone?.let {
            canvas.drawText(it, 30f, y, paint)
        }
        
        // Skills in sidebar
        if (resumeData.skills.isNotEmpty()) {
            y += 50f
            paint.textSize = 14f
            canvas.drawText("SKILLS", 30f, y, paint)
            
            paint.textSize = 12f
            resumeData.skills.forEach { skill ->
                y += 20f
                canvas.drawText(skill.name ?: "", 30f, y, paint)
            }
        }
        
        // Main content
        paint.color = android.graphics.Color.BLACK
        y = 50f
        
        // Header
        paint.textSize = 24f
        canvas.drawText(resumeData.personalInfo.fullName ?: "Your Name", 180f, y, paint)
        
        y += 30f
        paint.textSize = 16f
        canvas.drawText(resumeData.personalInfo.jobTitle ?: "Your Job Title", 180f, y, paint)
        
        // Summary
        y += 50f
        paint.textSize = 14f
        canvas.drawText("PROFESSIONAL SUMMARY", 180f, y, paint)
        
        y += 20f
        paint.textSize = 12f
        resumeData.personalInfo.summary?.let {
            canvas.drawText(it, 180f, y, paint)
        }
        
        // Experience
        if (resumeData.experiences.isNotEmpty()) {
            y += 50f
            paint.textSize = 14f
            canvas.drawText("WORK EXPERIENCE", 180f, y, paint)
            
            resumeData.experiences.forEach { exp ->
                y += 30f
                paint.textSize = 12f
                paint.isFakeBoldText = true
                canvas.drawText(exp.jobTitle ?: "", 180f, y, paint)
                
                y += 20f
                paint.isFakeBoldText = false
                canvas.drawText("${exp.company ?: ""} | ${exp.startDate ?: ""} - ${exp.endDate ?: "Present"}", 180f, y, paint)
                
                exp.bulletPoints?.forEach { bullet ->
                    y += 20f
                    canvas.drawText("• $bullet", 200f, y, paint)
                }
            }
        }
    }
}
