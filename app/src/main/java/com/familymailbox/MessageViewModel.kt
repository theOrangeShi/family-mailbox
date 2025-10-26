package com.familymailbox

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.net.Uri
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.provider.OpenableColumns
import java.io.*

class MessageViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MessageRepository
    private var syncRepository: SyncRepository? = null
    val allMessages: LiveData<List<Message>>
    
    // 是否启用服务器同步
    private var syncEnabled = false

    init {
        val messageDao = MessageDatabase.getDatabase(application).messageDao()
        repository = MessageRepository(messageDao)
        allMessages = repository.allMessages
        
        // 检查是否配置了服务器
        val prefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val serverUrl = prefs.getString("server_url", "")
        if (!serverUrl.isNullOrEmpty()) {
            enableSync(serverUrl)
        }
    }
    
    fun enableSync(serverUrl: String) {
        val apiService = ApiService(serverUrl)
        val messageDao = MessageDatabase.getDatabase(getApplication()).messageDao()
        syncRepository = SyncRepository(getApplication(), apiService, messageDao)
        syncEnabled = true
        
        // 保存服务器地址
        val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("server_url", serverUrl).apply()
        
        // 立即同步
        syncMessages()
    }
    
    fun disableSync() {
        syncEnabled = false
        syncRepository = null
        
        // 清除服务器地址
        val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("server_url").apply()
    }
    
    fun syncMessages() {
        if (syncEnabled && syncRepository != null) {
            viewModelScope.launch {
                syncRepository?.syncMessages()
            }
        }
    }
    
    fun getSyncStatus(): LiveData<SyncStatus>? {
        return syncRepository?.syncStatus
    }

    fun insertMessage(text: String, attachmentUri: Uri?, attachmentType: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (syncEnabled && syncRepository != null) {
                // 如果启用同步，发送到服务器
                val attachmentFile = if (attachmentUri != null && attachmentType != null) {
                    val tempFile = saveAttachmentToTemp(attachmentUri, attachmentType)
                    tempFile
                } else {
                    null
                }
                
                syncRepository?.sendMessage(text, attachmentFile, attachmentType)
            } else {
                // 否则只保存到本地
                val attachmentPath = if (attachmentUri != null && attachmentType != null) {
                    saveAttachment(attachmentUri, attachmentType)
                } else {
                    null
                }
                
                val message = Message(
                    text = text,
                    attachmentPath = attachmentPath,
                    attachmentType = attachmentType
                )
                repository.insert(message)
            }
        }
    }
    
    private fun saveAttachmentToTemp(uri: Uri, type: String): File? {
        return try {
            val context = getApplication<Application>()
            val timestamp = System.currentTimeMillis()
            val tempFile = File(context.cacheDir, "temp_$timestamp")
            
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun saveAttachment(uri: Uri, type: String): String? {
        return try {
            val context = getApplication<Application>()
            val timestamp = System.currentTimeMillis()
            
            val fileName = when (type) {
                "image" -> {
                    // 处理图片，修复旋转并保存
                    saveImageWithRotation(uri, timestamp)
                }
                "video" -> {
                    // 保存视频，保留原始文件名和格式
                    val originalFileName = getFileName(uri)
                    val fileName = if (originalFileName.isNotEmpty()) {
                        originalFileName
                    } else {
                        "video_$timestamp.mp4"
                    }
                    saveFile(uri, fileName, "image") // 视频保存到 image 文件夹，显示在公共区域
                }
                "file" -> {
                    // 获取原始文件名
                    val originalFileName = getFileName(uri)
                    val fileName = if (originalFileName.isNotEmpty()) {
                        originalFileName
                    } else {
                        "file_$timestamp"
                    }
                    saveFile(uri, fileName)
                }
                else -> null
            }
            fileName
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun saveImageWithRotation(uri: Uri, timestamp: Long): String? {
        return try {
            val context = getApplication<Application>()
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            
            // 读取原始图片
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            
            // 读取 EXIF 信息以获取旋转角度
            val rotatedBitmap = try {
                val exifStream = context.contentResolver.openInputStream(uri)
                val exif = exifStream?.let { ExifInterface(it) }
                val rotation = when (exif?.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> -1
                    ExifInterface.ORIENTATION_FLIP_VERTICAL -> -2
                    else -> 0
                }
                exifStream?.close()
                
                // 旋转图片
                if (rotation != 0) {
                    rotateBitmap(originalBitmap, rotation)
                } else {
                    originalBitmap
                }
            } catch (e: Exception) {
                originalBitmap
            }
            
            // 保存旋转后的图片
            val fileName = "image_$timestamp.jpg"
            val file = File(context.getExternalFilesDir("image"), fileName)
            val outputStream = FileOutputStream(file)
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.close()
            
            originalBitmap.recycle()
            if (rotatedBitmap != originalBitmap) {
                rotatedBitmap.recycle()
            }
            
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
        if (degrees == 0) return bitmap
        
        val matrix = android.graphics.Matrix()
        when (degrees) {
            90 -> matrix.postRotate(90f)
            180 -> matrix.postRotate(180f)
            270 -> matrix.postRotate(270f)
            -1 -> matrix.postScale(-1f, 1f) // 水平翻转
            -2 -> matrix.postScale(1f, -1f) // 垂直翻转
        }
        
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    
    private fun getFileName(uri: Uri): String {
        val context = getApplication<Application>()
        var result = ""
        try {
            if (uri.scheme == "content") {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1) {
                            result = it.getString(nameIndex) ?: ""
                        }
                    }
                }
            }
            
            if (result.isEmpty()) {
                result = uri.path?.let { path ->
                    File(path).name
                } ?: ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
    
    private fun saveFile(uri: Uri, fileName: String, folder: String = "file"): String? {
        return try {
            val context = getApplication<Application>()
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.getExternalFilesDir(folder), fileName)
            
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun getAttachmentPath(message: Message): String? {
        return message.attachmentPath
    }
}

class MessageRepository(private val messageDao: MessageDao) {
    val allMessages: LiveData<List<Message>> = messageDao.getAllMessages()

    suspend fun insert(message: Message) {
        messageDao.insert(message)
    }
}
