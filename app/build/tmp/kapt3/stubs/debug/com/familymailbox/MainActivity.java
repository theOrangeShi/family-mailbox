package com.familymailbox;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\u0015\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000b\u0018\u0000 52\u00020\u0001:\u00015B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0011\u001a\u00020\u0012H\u0002J\b\u0010\u0013\u001a\u00020\u0012H\u0002J\u0010\u0010\u0014\u001a\u00020\u00122\u0006\u0010\u0015\u001a\u00020\u000bH\u0002J\u0012\u0010\u0016\u001a\u00020\b2\b\u0010\u0017\u001a\u0004\u0018\u00010\rH\u0002J\b\u0010\u0018\u001a\u00020\u0012H\u0002J\"\u0010\u0019\u001a\u00020\u00122\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001b2\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eH\u0014J\u0012\u0010\u001f\u001a\u00020\u00122\b\u0010 \u001a\u0004\u0018\u00010!H\u0014J+\u0010\"\u001a\u00020\u00122\u0006\u0010\u001a\u001a\u00020\u001b2\f\u0010#\u001a\b\u0012\u0004\u0012\u00020\u000b0$2\u0006\u0010%\u001a\u00020&H\u0016\u00a2\u0006\u0002\u0010\'J\b\u0010(\u001a\u00020\u0012H\u0014J\u0010\u0010)\u001a\u00020\u00122\u0006\u0010*\u001a\u00020+H\u0002J\b\u0010,\u001a\u00020\u0012H\u0002J\b\u0010-\u001a\u00020\u0012H\u0002J\b\u0010.\u001a\u00020\u0012H\u0002J\b\u0010/\u001a\u00020\u0012H\u0002J\b\u00100\u001a\u00020\u0012H\u0002J\b\u00101\u001a\u00020\u0012H\u0002J\b\u00102\u001a\u00020\u0012H\u0002J\b\u00103\u001a\u00020\u0012H\u0002J\u0010\u00104\u001a\u00020\u00122\u0006\u0010\u0015\u001a\u00020\u000bH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u00066"}, d2 = {"Lcom/familymailbox/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "adapter", "Lcom/familymailbox/MessageAdapter;", "binding", "Lcom/familymailbox/databinding/ActivityMainBinding;", "isFirstLoad", "", "isPickingMedia", "selectedAttachmentType", "", "selectedFileUri", "Landroid/net/Uri;", "selectedImageUri", "viewModel", "Lcom/familymailbox/MessageViewModel;", "autoSyncMessages", "", "checkFirstLaunch", "connectToServer", "userName", "isVideo", "uri", "observeMessages", "onActivityResult", "requestCode", "", "resultCode", "data", "Landroid/content/Intent;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onRequestPermissionsResult", "permissions", "", "grantResults", "", "(I[Ljava/lang/String;[I)V", "onResume", "openAttachment", "message", "Lcom/familymailbox/Message;", "pickFile", "pickImage", "removeAttachment", "sendMessage", "setupListeners", "setupRecyclerView", "showServerInfoDialog", "showWelcomeDialog", "showWelcomeMessageDialog", "Companion", "app_debug"})
public final class MainActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.familymailbox.databinding.ActivityMainBinding binding;
    private com.familymailbox.MessageViewModel viewModel;
    private com.familymailbox.MessageAdapter adapter;
    @org.jetbrains.annotations.Nullable()
    private android.net.Uri selectedImageUri;
    @org.jetbrains.annotations.Nullable()
    private android.net.Uri selectedFileUri;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String selectedAttachmentType;
    private boolean isFirstLoad = true;
    private boolean isPickingMedia = false;
    private static final int REQUEST_CODE_PICK_IMAGE = 100;
    private static final int REQUEST_CODE_PICK_FILE = 101;
    private static final int PERMISSION_REQUEST_CODE = 200;
    @org.jetbrains.annotations.NotNull()
    public static final com.familymailbox.MainActivity.Companion Companion = null;
    
    public MainActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupRecyclerView() {
    }
    
    private final void setupListeners() {
    }
    
    private final void showServerInfoDialog() {
    }
    
    private final void observeMessages() {
    }
    
    private final void sendMessage() {
    }
    
    private final void pickImage() {
    }
    
    private final void pickFile() {
    }
    
    @java.lang.Override()
    protected void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable()
    android.content.Intent data) {
    }
    
    private final void removeAttachment() {
    }
    
    private final boolean isVideo(android.net.Uri uri) {
        return false;
    }
    
    private final void openAttachment(com.familymailbox.Message message) {
    }
    
    private final void checkFirstLaunch() {
    }
    
    private final void showWelcomeDialog() {
    }
    
    private final void showWelcomeMessageDialog(java.lang.String userName) {
    }
    
    private final void connectToServer(java.lang.String userName) {
    }
    
    private final void autoSyncMessages() {
    }
    
    @java.lang.Override()
    protected void onResume() {
    }
    
    @java.lang.Override()
    public void onRequestPermissionsResult(int requestCode, @org.jetbrains.annotations.NotNull()
    java.lang.String[] permissions, @org.jetbrains.annotations.NotNull()
    int[] grantResults) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/familymailbox/MainActivity$Companion;", "", "()V", "PERMISSION_REQUEST_CODE", "", "REQUEST_CODE_PICK_FILE", "REQUEST_CODE_PICK_IMAGE", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}