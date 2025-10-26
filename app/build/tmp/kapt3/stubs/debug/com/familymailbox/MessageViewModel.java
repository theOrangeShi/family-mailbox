package com.familymailbox;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000r\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\u0015J\u0010\u0010\u0016\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0017\u001a\u00020\bJ\u0010\u0010\u0018\u001a\u00020\u00152\u0006\u0010\u0019\u001a\u00020\u001aH\u0002J\u000e\u0010\u001b\u001a\n\u0012\u0004\u0012\u00020\u001c\u0018\u00010\u0006J\"\u0010\u001d\u001a\u00020\u00122\u0006\u0010\u001e\u001a\u00020\u00152\b\u0010\u001f\u001a\u0004\u0018\u00010\u001a2\b\u0010 \u001a\u0004\u0018\u00010\u0015J\u0018\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020\"2\u0006\u0010$\u001a\u00020%H\u0002J\u001a\u0010&\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\'\u001a\u00020\u0015H\u0002J\u001a\u0010(\u001a\u0004\u0018\u00010)2\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\'\u001a\u00020\u0015H\u0002J$\u0010*\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010+\u001a\u00020\u00152\b\b\u0002\u0010,\u001a\u00020\u0015H\u0002J\u001a\u0010-\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010.\u001a\u00020/H\u0002J\u0006\u00100\u001a\u00020\u0012R\u001d\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u00061"}, d2 = {"Lcom/familymailbox/MessageViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "allMessages", "Landroidx/lifecycle/LiveData;", "", "Lcom/familymailbox/Message;", "getAllMessages", "()Landroidx/lifecycle/LiveData;", "repository", "Lcom/familymailbox/MessageRepository;", "syncEnabled", "", "syncRepository", "Lcom/familymailbox/SyncRepository;", "disableSync", "", "enableSync", "serverUrl", "", "getAttachmentPath", "message", "getFileName", "uri", "Landroid/net/Uri;", "getSyncStatus", "Lcom/familymailbox/SyncStatus;", "insertMessage", "text", "attachmentUri", "attachmentType", "rotateBitmap", "Landroid/graphics/Bitmap;", "bitmap", "degrees", "", "saveAttachment", "type", "saveAttachmentToTemp", "Ljava/io/File;", "saveFile", "fileName", "folder", "saveImageWithRotation", "timestamp", "", "syncMessages", "app_debug"})
public final class MessageViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.familymailbox.MessageRepository repository = null;
    @org.jetbrains.annotations.Nullable()
    private com.familymailbox.SyncRepository syncRepository;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.familymailbox.Message>> allMessages = null;
    private boolean syncEnabled = false;
    
    public MessageViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.familymailbox.Message>> getAllMessages() {
        return null;
    }
    
    public final void enableSync(@org.jetbrains.annotations.NotNull()
    java.lang.String serverUrl) {
    }
    
    public final void disableSync() {
    }
    
    public final void syncMessages() {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final androidx.lifecycle.LiveData<com.familymailbox.SyncStatus> getSyncStatus() {
        return null;
    }
    
    public final void insertMessage(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.Nullable()
    android.net.Uri attachmentUri, @org.jetbrains.annotations.Nullable()
    java.lang.String attachmentType) {
    }
    
    private final java.io.File saveAttachmentToTemp(android.net.Uri uri, java.lang.String type) {
        return null;
    }
    
    private final java.lang.String saveAttachment(android.net.Uri uri, java.lang.String type) {
        return null;
    }
    
    private final java.lang.String saveImageWithRotation(android.net.Uri uri, long timestamp) {
        return null;
    }
    
    private final android.graphics.Bitmap rotateBitmap(android.graphics.Bitmap bitmap, int degrees) {
        return null;
    }
    
    private final java.lang.String getFileName(android.net.Uri uri) {
        return null;
    }
    
    private final java.lang.String saveFile(android.net.Uri uri, java.lang.String fileName, java.lang.String folder) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getAttachmentPath(@org.jetbrains.annotations.NotNull()
    com.familymailbox.Message message) {
        return null;
    }
}