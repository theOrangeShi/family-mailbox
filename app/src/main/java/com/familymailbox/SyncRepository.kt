package com.familymailbox

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 同步仓库，负责本地数据库和服务器之间的同步
 */
class SyncRepository(
    private val context: Context,
    private val apiService: ApiService,
    private val localDao: MessageDao
) {

    private val _syncStatus = MutableLiveData<SyncStatus>()
    val syncStatus: LiveData<SyncStatus> = _syncStatus

    /**
     * 同步消息：从服务器获取并更新本地数据库
     */
    suspend fun syncMessages(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _syncStatus.postValue(SyncStatus.Syncing)
            
            // 从服务器获取消息
            val result = apiService.getMessages()
            if (result.isSuccess) {
                val serverMessages = result.getOrNull() ?: emptyList()
                
                // 清空本地数据库
                localDao.deleteAll()
                
                // 插入服务器消息到本地数据库
                for (serverMsg in serverMessages) {
                    val localMessage = Message(
                        id = serverMsg.id,
                        text = serverMsg.text,
                        attachmentPath = serverMsg.attachmentPath?.let { 
                            downloadAttachmentIfNeeded(it, serverMsg.attachmentType)
                        },
                        attachmentType = serverMsg.attachmentType
                    )
                    localDao.insert(localMessage)
                }
                
                _syncStatus.postValue(SyncStatus.Success)
                Result.success(Unit)
            } else {
                _syncStatus.postValue(SyncStatus.Error(result.exceptionOrNull()?.message ?: "Unknown error"))
                Result.failure(result.exceptionOrNull() ?: Exception("Sync failed"))
            }
        } catch (e: Exception) {
            _syncStatus.postValue(SyncStatus.Error(e.message ?: "Unknown error"))
            Result.failure(e)
        }
    }

    /**
     * 发送消息到服务器
     */
    suspend fun sendMessage(text: String, attachmentFile: File?, attachmentType: String?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val result = apiService.createMessage(text, attachmentFile, attachmentType)
            if (result.isSuccess) {
                // 同步所有消息
                syncMessages()
                Result.success(Unit)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Send failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 下载附件（如果需要）
     */
    private suspend fun downloadAttachmentIfNeeded(filename: String, attachmentType: String?): String {
        val folder = when (attachmentType) {
            "image", "video" -> "image"
            else -> "file"
        }
        
        val localFile = File(context.getExternalFilesDir(folder), filename)
        
        // 如果文件已存在，直接返回路径
        if (localFile.exists()) {
            return localFile.absolutePath
        }
        
        // 否则从服务器下载
        val result = apiService.downloadAttachment(filename, localFile)
        return if (result.isSuccess) {
            localFile.absolutePath
        } else {
            "" // 下载失败返回空字符串
        }
    }
}

/**
 * 同步状态
 */
sealed class SyncStatus {
    object Idle : SyncStatus()
    object Syncing : SyncStatus()
    object Success : SyncStatus()
    data class Error(val message: String) : SyncStatus()
}



