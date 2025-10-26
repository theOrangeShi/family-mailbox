# 家庭留言箱

一个温馨的 Android 应用，用于家庭成员之间静默保存留言和回忆。

## 功能特点

- 💬 聊天式发送界面
- 📝 支持文字留言
- 📷 支持选择图片
- 📎 支持选择文件
- ✨ 静默保存，不打扰
- ❤️ 不留时间戳，不显示发送人
- 💾 本地持久化存储
- 🎨 简洁美观的 Material Design 界面

## 使用方法

1. 在底部输入框输入文字（可选）
2. 点击相机图标选择图片（可选）
3. 点击上传图标选择文件（可选）
4. 点击发送按钮即可
5. 所有留言会显示在公共区域

## 构建说明

使用 Android Studio 或 Gradle 构建 APK：

```bash
./gradlew assembleRelease
```

生成的 APK 位于：`app/build/outputs/apk/release/app-release.apk` 