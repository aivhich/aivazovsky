package com.aivhich.aivazovsky

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaquo.python.Python
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.io.Serial

class MainVM constructor(val context: Context): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun saveImageToInternalStorage(inputStream: InputStream?, folderName: String, fileName: String, context: Context): String {
        val file = File(context.filesDir, folderName)
        if (!file.exists()) {
            file.mkdirs()
        }

        val imageFile = File(file, fileName)
        imageFile.outputStream().use { output ->
            inputStream?.copyTo(output)
        }

        return imageFile.absolutePath
    }



    fun sendToPrint(selectedImage:Uri){
        viewModelScope.launch(Dispatchers.Default) {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(selectedImage)
            val realPath = saveImageToInternalStorage(
                inputStream,
                "chaquopy/AssetFinder/app/images/",
                "choosedimage.jpg",
                context
            )

            val python = Python.getInstance()
            val lines = python
                .getModule("linedraw")
                .callAttr("sketch", realPath)
                .toString().replace("[[", "").replace("]]", "").replace("], [", "@")
            System.out.println(lines)
            for(pl in lines.split("), (")){
                System.out.println(pl+"!")
                dataExchaneInstance?.write((pl+"!").replace("(", "").replace(")", "").toByteArray())
                while (true){
                    System.out.println("wait")
                    if(dataExchaneInstance?.read()=="+"){
                        System.out.println("+")
                        break
                    }
                    ///
                }
            }
        }
    }
}