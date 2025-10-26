package com.familymailbox

import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * API服务类，处理与服务器的通信
 */
class ApiService(private val baseUrl: String) {

    companion object {
        private const val TIMEOUT = 30000 // 30秒超时
    }

    /**
     * 获取所有消息
     */
    suspend fun getMessages(): Result<List<MessageDto>> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/api/messages")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = TIMEOUT
            connection.readTimeout = TIMEOUT
            connection.setRequestProperty("Content-Type", "application/json")

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                val jsonObject = JSONObject(response)
                if (jsonObject.getBoolean("success")) {
                    val messagesArray = jsonObject.getJSONArray("messages")
                    val messages = mutableListOf<MessageDto>()
                    
                    for (i in 0 until messagesArray.length()) {
                        val msgObj = messagesArray.getJSONObject(i)
                        messages.add(MessageDto(
                            id = msgObj.getLong("id"),
                            text = msgObj.getString("text"),
                            attachmentPath = if (msgObj.isNull("attachmentPath")) null else msgObj.getString("attachmentPath"),
                            attachmentType = if (msgObj.isNull("attachmentType")) null else msgObj.getString("attachmentType")
                        ))
                    }
                    
                    Result.success(messages)
                } else {
                    Result.failure(Exception("Failed to fetch messages"))
                }
            } else {
                Result.failure(Exception("HTTP error: $responseCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 创建新消息
     */
    suspend fun createMessage(text: String, attachmentFile: File?, attachmentType: String?): Result<MessageDto> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/api/messages")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.connectTimeout = TIMEOUT
            connection.readTimeout = TIMEOUT
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            // 构建请求体
            val jsonObject = JSONObject()
            jsonObject.put("text", text)
            
            if (attachmentFile != null && attachmentType != null) {
                // 读取文件并转换为Base64
                val fileBytes = attachmentFile.readBytes()
                val base64 = Base64.encodeToString(fileBytes, Base64.NO_WRAP)
                jsonObject.put("attachment", base64)
                jsonObject.put("attachmentType", attachmentType)
            }

            // 发送请求
            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(jsonObject.toString())
            writer.flush()
            writer.close()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                val responseJson = JSONObject(response)
                if (responseJson.getBoolean("success")) {
                    val msgObj = responseJson.getJSONObject("message")
                    val message = MessageDto(
                        id = msgObj.getLong("id"),
                        text = msgObj.getString("text"),
                        attachmentPath = if (msgObj.isNull("attachmentPath")) null else msgObj.getString("attachmentPath"),
                        attachmentType = if (msgObj.isNull("attachmentType")) null else msgObj.getString("attachmentType")
                    )
                    Result.success(message)
                } else {
                    Result.failure(Exception("Failed to create message"))
                }
            } else {
                Result.failure(Exception("HTTP error: $responseCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 下载附件
     */
    suspend fun downloadAttachment(filename: String, destinationFile: File): Result<File> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/api/attachments/$filename")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = TIMEOUT
            connection.readTimeout = TIMEOUT

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.use { input ->
                    destinationFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                Result.success(destinationFile)
            } else {
                Result.failure(Exception("HTTP error: $responseCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 健康检查
     */
    suspend fun healthCheck(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/api/health")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Result.success(true)
            } else {
                Result.failure(Exception("Server not healthy"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * 消息数据传输对象
 */
data class MessageDto(
    val id: Long,
    val text: String,
    val attachmentPath: String?,
    val attachmentType: String?
)



