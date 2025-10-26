# 家庭留言箱配置指南

## 📝 编译前配置

在编译 APK 之前，您需要修改配置文件以适配您的家庭环境。

### 1. 打开配置文件

编辑文件：`app/src/main/res/values/config.xml`

### 2. 配置服务器地址

将 `default_server_url` 修改为您的服务器地址：

```xml
<!-- 方式1: 使用域名（推荐，需要配置HTTPS） -->
<string name="default_server_url">https://your-domain.com</string>

<!-- 方式2: 使用IP地址和端口 -->
<string name="default_server_url">http://192.168.1.100:5000</string>

<!-- 方式3: 不使用服务器同步（本地模式）(可用于调试) -->
<string name="default_server_url"></string>
```

### 3. 配置家庭成员名单

在 `family_members` 中添加您的家庭成员姓名，用逗号分隔：

```xml
<string name="family_members">爸爸,妈妈,儿子,女儿,爷爷,奶奶</string>
```

**注意**：
- 姓名需要**完全匹配**才能连接到服务器
- 姓名区分大小写
- 不在名单中的用户将使用本地模式，消息不会同步

### 4. 配置验证模式

```xml
<!-- 启用姓名验证（推荐）：只有白名单中的姓名可以连接服务器 -->
<bool name="enable_name_verification">true</bool>

<!-- 关闭姓名验证：所有人都可以连接服务器 -->
<bool name="enable_name_verification">false</bool>
```

## 📱 用户首次使用流程

### 启用姓名验证时（`enable_name_verification = true`）

1. **首次打开应用** → 显示欢迎界面
2. **输入姓名** → 用户输入自己的姓名
3. **验证姓名**：
   - ✅ **姓名在白名单中** → 自动连接到预配置的服务器，消息云端同步
   - ❌ **姓名不在白名单中** → 使用本地模式，消息仅保存在本地

### 关闭姓名验证时（`enable_name_verification = false`）

1. **首次打开应用** → 显示欢迎界面
2. **输入姓名** → 用户输入自己的姓名（仅用于识别）
3. **自动连接** → 所有人都自动连接到预配置的服务器

## 🔧 配置示例

### 示例 1：私密家庭使用（推荐）

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- 使用您的服务器地址 -->
    <string name="default_server_url">https://family.example.com</string>
    
    <!-- 只有这4个家庭成员可以同步 -->
    <string name="family_members">张三,李四,王五,赵六</string>
    
    <!-- 启用验证 -->
    <bool name="enable_name_verification">true</bool>
</resources>
```

**效果**：
- 张三、李四、王五、赵六输入姓名后自动连接服务器
- 其他人（如"小明"）输入姓名后只能使用本地模式
- 保护隐私，防止非家庭成员访问

### 示例 2：公开使用（不推荐）

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="default_server_url">https://family.example.com</string>
    
    <!-- 这个配置会被忽略 -->
    <string name="family_members"></string>
    
    <!-- 关闭验证，所有人都可以连接 -->
    <bool name="enable_name_verification">false</bool>
</resources>
```

**效果**：
- 任何人输入任何姓名都会连接到服务器
- 适合非私密场景

### 示例 3：纯本地模式（调试专用）

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- 留空表示不使用服务器 -->
    <string name="default_server_url"></string>
    
    <string name="family_members"></string>
    
    <bool name="enable_name_verification">true</bool>
</resources>
```

**效果**：
- 所有用户只能使用本地模式
- 消息不会同步，每个设备独立


### 手动修改服务器地址
2. 首次打开时输入自己的姓名
3. 如果姓名在白名单中，自动连接服务器
4. 开始使用，消息自动同步

### 3. 添加新成员

如果需要添加新的家庭成员：
1. 修改 `config.xml` 中的 `family_members`
2. 重新编译 APK
3. 分发新的 APK 给所有人（或只给新成员）

## ⚠️ 重要提示

1. **服务器地址必须正确**：确保服务器已部署并可访问
2. **HTTPS 推荐**：使用 HTTPS 保护数据传输安全
3. **姓名匹配**：姓名必须与配置文件中的完全一致（区分大小写）
4. **备份重要**：定期备份服务器数据
5. **隐私保护**：不要将 APK 分发给非家庭成员

## 🆘 故障排除

### 问题：输入姓名后没有连接到服务器

**检查**：
1. 姓名是否在 `family_members` 列表中
2. 姓名拼写是否完全正确（包括大小写）
3. `enable_name_verification` 是否为 `true`
4. 服务器地址是否正确

### 问题：连接服务器失败

**检查**：
1. 服务器是否正在运行
2. 网络连接是否正常
3. 防火墙是否允许访问
4. 服务器地址格式是否正确（包含 `http://` 或 `https://`）

### 问题：想要重新输入姓名

**解决方法**：
1. 打开应用设置
2. 清除应用数据
3. 重新打开应用
4. 重新输入姓名




