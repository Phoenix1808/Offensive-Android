package com.example.offensiveandroid.ui.theme

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context

class GetClipboard(private val context: Context) {
    @SuppressLint("SuspiciousIndentation")
    fun getClipboardData(): String{
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipdata = clipboardManager.primaryClip //retrieves current content
        if (clipdata != null && clipdata.itemCount>0){
            val clipItem = clipdata.getItemAt(0)
            val text = clipItem.text.toString() //text extraction

            return if (text.isNotEmpty()){
                text
            } else{
                "ClipBoard contains no significant data"
            }
        }
        return "ClipBoard is empty o contains non-text data"
    }
}