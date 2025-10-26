package com.familymailbox

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.familymailbox.databinding.ActivityMainBinding
import io.noties.markwon.Markwon
import io.noties.markwon.core.MarkwonTheme
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MessageViewModel
    private lateinit var adapter: MessageAdapter
    private var selectedImageUri: Uri? = null
    private var selectedFileUri: Uri? = null
    private var selectedAttachmentType: String? = null
    private var isFirstLoad = true
    private var isPickingMedia = false  // 标记是否正在选择媒体文件

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 100
        private const val REQUEST_CODE_PICK_FILE = 101
        private const val PERMISSION_REQUEST_CODE = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MessageViewModel::class.java]
        setupRecyclerView()
        setupListeners()
        observeMessages()
        
        // 首次加载后滚动到顶部（只执行一次）
        if (isFirstLoad) {
            binding.recyclerView.postDelayed({
                binding.recyclerView.scrollToPosition(0)
                isFirstLoad = false
            }, 100)
        }
        
        // 检查首次启动和姓名验证
        checkFirstLaunch()
        
        // 自动同步消息
        autoSyncMessages()

        // 请求权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), PERMISSION_REQUEST_CODE)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = MessageAdapter { message ->
            // 点击消息时的处理
            if (message.attachmentType != null && message.attachmentPath != null) {
                openAttachment(message)
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        // 设置工具栏菜单监听
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_server_settings -> {
                    showServerInfoDialog()
                    true
                }
                else -> false
            }
        }
        
        // 延迟缩小工具栏图标（等待菜单加载完成）
        binding.toolbar.post {
            try {
                val toolbar = binding.toolbar
                for (i in 0 until toolbar.childCount) {
                    val child = toolbar.getChildAt(i)
                    if (child is androidx.appcompat.widget.ActionMenuView) {
                        for (j in 0 until child.childCount) {
                            val menuItem = child.getChildAt(j)
                            menuItem.layoutParams?.apply {
                                width = (24 * resources.displayMetrics.density).toInt()
                                height = (24 * resources.displayMetrics.density).toInt()
                            }
                            if (menuItem is android.widget.ImageView) {
                                menuItem.scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
                                menuItem.setPadding(4, 4, 4, 4)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // 忽略错误
            }
        }
        
        binding.btnSend.setOnClickListener {
            sendMessage()
        }
        
        binding.btnImage.setOnClickListener {
            pickImage()
        }
        
        binding.btnFile.setOnClickListener {
            pickFile()
        }
        
        binding.btnRemoveImage.setOnClickListener {
            removeAttachment()
        }
    }
    
    private fun showServerInfoDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_server_settings, null)
        val serverUrlInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.server_url_input)
        val btnTestConnection = dialogView.findViewById<android.widget.Button>(R.id.btn_test_connection)
        val btnCancel = dialogView.findViewById<android.widget.Button>(R.id.btn_cancel)
        val btnSave = dialogView.findViewById<android.widget.Button>(R.id.btn_save)
        
        // 加载当前服务器地址
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val currentUrl = prefs.getString("server_url", "")
        serverUrlInput.setText(currentUrl)
        
        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        
        btnTestConnection.setOnClickListener {
            val url = serverUrlInput.text.toString().trim()
            if (url.isEmpty() || url == "http://your-server-ip:5000") {
                android.widget.Toast.makeText(this, "服务器地址未配置", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // 测试连接
            lifecycleScope.launch {
                val apiService = ApiService(url)
                val result = apiService.healthCheck()
                withContext(Dispatchers.Main) {
                    if (result.isSuccess) {
                        android.widget.Toast.makeText(this@MainActivity, "连接成功！", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        android.widget.Toast.makeText(this@MainActivity, "连接失败: ${result.exceptionOrNull()?.message}", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        btnSave.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }

    private fun observeMessages() {
        viewModel.allMessages.observe(this) { messages ->
            // 只更新列表，不做任何滚动操作
            adapter.submitList(messages.reversed())
        }
    }

    private fun sendMessage() {
        val text = binding.editText.text.toString().trim()
        
        if (text.isEmpty() && selectedImageUri == null && selectedFileUri == null) {
            return
        }

        val messageContent = if (text.isNotEmpty()) text else "📎"
        
        val attachmentUri = selectedImageUri ?: selectedFileUri
        
        viewModel.insertMessage(messageContent, attachmentUri, selectedAttachmentType)
        
        // 清除输入
        binding.editText.setText("")
        removeAttachment()
    }

    private fun pickImage() {
        isPickingMedia = true  // 标记开始选择
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/* video/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    private fun pickFile() {
        isPickingMedia = true  // 标记开始选择
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, REQUEST_CODE_PICK_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        // 重置标记
        isPickingMedia = false
        
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_CODE_PICK_IMAGE -> {
                    selectedImageUri = data.data
                    // 判断是图片还是视频
                    selectedAttachmentType = if (isVideo(data.data)) "video" else "image"
                    selectedFileUri = null
                    // 显示照相机图标
                    binding.imagePreview.setImageResource(android.R.drawable.ic_menu_camera)
                    binding.imagePreviewContainer.visibility = View.VISIBLE
                    binding.imagePreview.visibility = View.VISIBLE
                    binding.btnRemoveImage.visibility = View.VISIBLE
                }
                REQUEST_CODE_PICK_FILE -> {
                    selectedFileUri = data.data
                    selectedAttachmentType = "file"
                    selectedImageUri = null
                    // 显示附件图标
                    binding.imagePreview.setImageResource(android.R.drawable.ic_menu_upload)
                    binding.imagePreviewContainer.visibility = View.VISIBLE
                    binding.imagePreview.visibility = View.VISIBLE
                    binding.btnRemoveImage.visibility = View.VISIBLE
                }
            }
        } else {
            // 用户取消选择时也重置标记
            isPickingMedia = false
        }
    }

    private fun removeAttachment() {
        selectedImageUri = null
        selectedFileUri = null
        selectedAttachmentType = null
        binding.imagePreviewContainer.visibility = View.GONE
        binding.imagePreview.visibility = View.GONE
        binding.btnRemoveImage.visibility = View.GONE
    }
    
    private fun isVideo(uri: Uri?): Boolean {
        return try {
            uri?.let {
                val mimeType = contentResolver.getType(it)
                mimeType?.startsWith("video/") == true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }
    
    private fun openAttachment(message: Message) {
        try {
            message.attachmentPath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    when (message.attachmentType) {
                        "image" -> {
                            // 打开全屏图片查看器
                            val intent = Intent(this, ImageActivity::class.java)
                            intent.putExtra("image_path", path)
                            startActivity(intent)
                        }
                        "file" -> {
                            // 打开文件
                            val uri = FileProvider.getUriForFile(
                                this,
                                "${packageName}.fileprovider",
                                file
                            )
                            
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(uri, "*/*")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            
                            val activities = intent.resolveActivity(packageManager)
                            if (activities != null) {
                                startActivity(intent)
                            } else {
                                android.widget.Toast.makeText(this, "无法打开文件", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    android.widget.Toast.makeText(this, "附件文件不存在", android.widget.Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                android.widget.Toast.makeText(this, "附件路径为空", android.widget.Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            android.widget.Toast.makeText(this, "打开附件失败: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkFirstLaunch() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val userName = prefs.getString("user_name", "")
        
        // 如果没有姓名，显示欢迎对话框
        if (userName.isNullOrEmpty()) {
            showWelcomeDialog()
        }
    }
    
    private fun showWelcomeDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_welcome, null)
        val nameInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.name_input)
        val btnContinue = dialogView.findViewById<android.widget.Button>(R.id.btn_continue)
        val hintText = dialogView.findViewById<android.widget.TextView>(R.id.hint_text)
        
        // 检查是否启用姓名验证
        val enableVerification = resources.getBoolean(R.bool.enable_name_verification)
        if (!enableVerification) {
            hintText.text = "输入您的姓名以便家人识别"
        }
        
        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        
        btnContinue.setOnClickListener {
            val name = nameInput.text.toString().trim()
            if (name.isEmpty()) {
                android.widget.Toast.makeText(this, "请输入姓名", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // 保存姓名
            val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
            prefs.edit().putString("user_name", name).apply()
            
            // 检查是否在家庭成员列表中
            if (enableVerification) {
                val familyMembers = resources.getString(R.string.family_members)
                    .split(",")
                    .map { it.trim() }
                
                if (familyMembers.contains(name)) {
                    // 在白名单中，显示欢迎消息对话框
                    dialog.dismiss()
                    showWelcomeMessageDialog(name)
                } else {
                    // 不在白名单中，无法使用
                    android.widget.Toast.makeText(
                        this, 
                        "抱歉，您不在家庭成员名单中", 
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                    // 3秒后关闭应用
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        finish()
                    }, 3000)
                }
            } else {
                // 不启用验证，显示欢迎消息对话框
                dialog.dismiss()
                showWelcomeMessageDialog(name)
            }
        }
        
        dialog.show()
    }
    
    private fun showWelcomeMessageDialog(userName: String) {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("is_first_launch", true)
        
        // 只在首次启动时显示
        if (!isFirstLaunch) {
            // 不是首次启动，直接连接服务器
            connectToServer(userName)
            return
        }
        
        val dialogView = layoutInflater.inflate(R.layout.dialog_welcome_message, null)
        val welcomeContent = dialogView.findViewById<TextView>(R.id.welcome_content)
        val btnOk = dialogView.findViewById<android.widget.Button>(R.id.btn_ok)
        
        // 使用 Markwon 渲染 Markdown
        val markwon = Markwon.create(this)
        val welcomeText = """## 微信的问题

**1. 无隐私 (Lack of Privacy)**
- 微信会收集大量的用户数据，包括个人信息、联系人、位置数据、甚至支付交易细节等
- 非端到端加密: 微信的消息传输虽然有加密，但缺乏像Signal或WhatsApp那样的**端到端加密**

**2. 内容审查和监控**
- 研究表明，微信平台对聊天内容、图片、文件等进行内容审查和政治监控

**3. 烦人的弹窗消息**（Annoying pop-up message）
- 消息泛滥: 微信集成了聊天、公众号、小程序、朋友圈、支付等多种功能
- 难以完全静音: 即使关闭了部分通知，仍难以完全避免所有不必要的"弹窗"或红点提醒

## 本APP特点

1. **完全干净**，无公众号、小程序、朋友圈
2. **不受潜在监控**
3. **无弹窗和通知**
4. **静默存储**，发送的留言不会发送任何提醒，只有在打开软件的时候才能接收到留言
5. **无时间戳**，发送的留言不会显示时间
6. **无发送人显示**，仅作为家人间共享
7. **无发送时间显示**，保留发送人的隐私
8. **本项目为github开源项目**，任何人均可部署并进行自定义"""
        
        markwon.setMarkdown(welcomeContent, welcomeText)
        
        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        
        btnOk.setOnClickListener {
            // 标记为已启动
            prefs.edit().putBoolean("is_first_launch", false).apply()
            
            // 连接服务器
            connectToServer(userName)
            
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun connectToServer(userName: String) {
        // 连接到服务器
        val serverUrl = resources.getString(R.string.default_server_url).trim()
        if (serverUrl.isNotEmpty() && serverUrl != "http://your-server-ip:5000" && serverUrl != "http://10.0.2.2:5001") {
            viewModel.enableSync(serverUrl)
            android.widget.Toast.makeText(
                this, 
                "欢迎 $userName！正在连接到家庭服务器...", 
                android.widget.Toast.LENGTH_LONG
            ).show()
        } else {
            android.widget.Toast.makeText(
                this, 
                "欢迎 $userName！请在设置中配置服务器地址", 
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun autoSyncMessages() {
        // 自动同步消息（静默，不显示 Toast）
        viewModel.syncMessages()
    }
    
    override fun onResume() {
        super.onResume()
        // 每次回到前台时自动同步（除非正在选择媒体文件）
        if (!isFirstLoad && !isPickingMedia) {
            autoSyncMessages()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 处理权限请求结果
    }
}

class MessageAdapter(
    private val onMessageClick: (Message) -> Unit
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    private var messages = emptyList<Message>()
    private var markwon: Markwon? = null

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.message_text)
        val attachmentMediaContainer: android.view.ViewGroup = view.findViewById(R.id.attachment_media_container)
        val attachmentImage: ImageView = view.findViewById(R.id.attachment_image)
        val attachmentVideo: VideoView = view.findViewById(R.id.attachment_video)
        val videoPlayOverlay: View = view.findViewById(R.id.video_play_overlay)
        val videoPlayButton: ImageView = view.findViewById(R.id.video_play_button)
        val attachmentFile: TextView = view.findViewById(R.id.attachment_file)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        
        // 初始化 Markwon，配置标题颜色为主题色
        if (markwon == null) {
            markwon = Markwon.builder(parent.context)
                .usePlugin(object : io.noties.markwon.AbstractMarkwonPlugin() {
                    override fun configureTheme(builder: MarkwonTheme.Builder) {
                        // 获取主题色
                        val typedValue = android.util.TypedValue()
                        parent.context.theme.resolveAttribute(
                            com.google.android.material.R.attr.colorPrimary,
                            typedValue,
                            true
                        )
                        val primaryColor = typedValue.data
                        
                        // 设置标题颜色为主题色
                        builder
                            .headingBreakHeight(0)
                            .headingTextSizeMultipliers(floatArrayOf(2f, 1.5f, 1.17f, 1f, 0.83f, 0.67f))
                            .apply {
                                // 使用反射设置标题颜色
                                try {
                                    val field = builder.javaClass.getDeclaredField("headingTextColor")
                                    field.isAccessible = true
                                    field.set(builder, primaryColor)
                                } catch (e: Exception) {
                                    // 如果反射失败，使用默认设置
                                }
                            }
                    }
                })
                .build()
        }
        
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        
        // 显示文字内容（支持 Markdown）
        if (message.text.isNotEmpty() && message.text != "📎") {
            holder.textView.visibility = View.VISIBLE
            // 使用 Markwon 渲染 Markdown
            markwon?.setMarkdown(holder.textView, message.text)
        } else {
            holder.textView.visibility = View.GONE
        }
        
        // 处理附件
        when (message.attachmentType) {
            "image", "video" -> {
                // 显示图片或视频
                message.attachmentPath?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
                        holder.attachmentMediaContainer.visibility = View.VISIBLE
                        
                        if (message.attachmentType == "image") {
                            // 显示图片
                            val bitmap = BitmapFactory.decodeFile(path)
                            holder.attachmentImage.setImageBitmap(bitmap)
                            holder.attachmentImage.visibility = View.VISIBLE
                            holder.attachmentVideo.visibility = View.GONE
                            holder.videoPlayOverlay.visibility = View.GONE
                            holder.videoPlayButton.visibility = View.GONE
                            
                            holder.attachmentMediaContainer.setOnClickListener {
                                onMessageClick(message)
                            }
                        } else {
                            // 显示视频
                            holder.attachmentImage.visibility = View.GONE
                            holder.attachmentVideo.visibility = View.VISIBLE
                            holder.videoPlayOverlay.visibility = View.VISIBLE
                            holder.videoPlayButton.visibility = View.VISIBLE
                            
                            holder.videoPlayOverlay.setOnClickListener {
                                holder.videoPlayOverlay.visibility = View.GONE
                                holder.videoPlayButton.visibility = View.GONE
                                holder.attachmentVideo.setVideoPath(path)
                                holder.attachmentVideo.start()
                            }
                            
                            holder.videoPlayButton.setOnClickListener {
                                holder.videoPlayOverlay.visibility = View.GONE
                                holder.videoPlayButton.visibility = View.GONE
                                holder.attachmentVideo.setVideoPath(path)
                                holder.attachmentVideo.start()
                            }
                            
                            holder.attachmentVideo.setOnClickListener {
                                if (holder.attachmentVideo.isPlaying) {
                                    holder.attachmentVideo.pause()
                                    holder.videoPlayOverlay.visibility = View.VISIBLE
                                    holder.videoPlayButton.visibility = View.VISIBLE
                                } else {
                                    holder.attachmentVideo.start()
                                }
                            }
                        }
                    } else {
                        holder.attachmentMediaContainer.visibility = View.GONE
                    }
                } ?: run {
                    holder.attachmentMediaContainer.visibility = View.GONE
                }
                holder.attachmentFile.visibility = View.GONE
            }
            "file" -> {
                // 显示文件信息
                holder.attachmentImage.visibility = View.GONE
                message.attachmentPath?.let { path ->
                    val file = File(path)
                    val fileName = file.name
                    holder.attachmentFile.text = "📎 $fileName"
                    holder.attachmentFile.visibility = View.VISIBLE
                    holder.attachmentFile.setOnClickListener {
                        onMessageClick(message)
                    }
                } ?: run {
                    holder.attachmentFile.visibility = View.GONE
                }
            }
            else -> {
                holder.attachmentMediaContainer.visibility = View.GONE
                holder.attachmentFile.visibility = View.GONE
            }
        }
    }

    override fun getItemCount() = messages.size

    fun submitList(newMessages: List<Message>) {
        val oldSize = messages.size
        val newSize = newMessages.size
        messages = newMessages
        
        // 智能更新，避免重置滚动位置
        when {
            oldSize == 0 -> {
                // 首次加载，全部刷新
                notifyDataSetChanged()
            }
            newSize > oldSize -> {
                // 有新消息，只通知新增的部分
                notifyItemRangeInserted(0, newSize - oldSize)
            }
            newSize < oldSize -> {
                // 消息减少，通知删除
                notifyItemRangeRemoved(0, oldSize - newSize)
            }
            else -> {
                // 数量相同，可能内容变化，只刷新变化的部分
                notifyItemRangeChanged(0, newSize)
            }
        }
    }
}
