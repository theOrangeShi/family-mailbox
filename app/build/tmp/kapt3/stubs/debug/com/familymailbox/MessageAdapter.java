package com.familymailbox;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0017B\u0019\u0012\u0012\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004\u00a2\u0006\u0002\u0010\u0007J\b\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u00022\u0006\u0010\u0010\u001a\u00020\rH\u0016J\u0018\u0010\u0011\u001a\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\rH\u0016J\u0014\u0010\u0015\u001a\u00020\u00062\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00050\u000bR\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lcom/familymailbox/MessageAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/familymailbox/MessageAdapter$ViewHolder;", "onMessageClick", "Lkotlin/Function1;", "Lcom/familymailbox/Message;", "", "(Lkotlin/jvm/functions/Function1;)V", "markwon", "Lio/noties/markwon/Markwon;", "messages", "", "getItemCount", "", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "submitList", "newMessages", "ViewHolder", "app_debug"})
public final class MessageAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.familymailbox.MessageAdapter.ViewHolder> {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function1<com.familymailbox.Message, kotlin.Unit> onMessageClick = null;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.familymailbox.Message> messages;
    @org.jetbrains.annotations.Nullable()
    private io.noties.markwon.Markwon markwon;
    
    public MessageAdapter(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.familymailbox.Message, kotlin.Unit> onMessageClick) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.familymailbox.MessageAdapter.ViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.familymailbox.MessageAdapter.ViewHolder holder, int position) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    public final void submitList(@org.jetbrains.annotations.NotNull()
    java.util.List<com.familymailbox.Message> newMessages) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0011\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0015\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\bR\u0011\u0010\u0017\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\fR\u0011\u0010\u0019\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001b\u00a8\u0006\u001d"}, d2 = {"Lcom/familymailbox/MessageAdapter$ViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "view", "Landroid/view/View;", "(Landroid/view/View;)V", "attachmentFile", "Landroid/widget/TextView;", "getAttachmentFile", "()Landroid/widget/TextView;", "attachmentImage", "Landroid/widget/ImageView;", "getAttachmentImage", "()Landroid/widget/ImageView;", "attachmentMediaContainer", "Landroid/view/ViewGroup;", "getAttachmentMediaContainer", "()Landroid/view/ViewGroup;", "attachmentVideo", "Landroid/widget/VideoView;", "getAttachmentVideo", "()Landroid/widget/VideoView;", "textView", "getTextView", "videoPlayButton", "getVideoPlayButton", "videoPlayOverlay", "getVideoPlayOverlay", "()Landroid/view/View;", "getView", "app_debug"})
    public static final class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull()
        private final android.view.View view = null;
        @org.jetbrains.annotations.NotNull()
        private final android.widget.TextView textView = null;
        @org.jetbrains.annotations.NotNull()
        private final android.view.ViewGroup attachmentMediaContainer = null;
        @org.jetbrains.annotations.NotNull()
        private final android.widget.ImageView attachmentImage = null;
        @org.jetbrains.annotations.NotNull()
        private final android.widget.VideoView attachmentVideo = null;
        @org.jetbrains.annotations.NotNull()
        private final android.view.View videoPlayOverlay = null;
        @org.jetbrains.annotations.NotNull()
        private final android.widget.ImageView videoPlayButton = null;
        @org.jetbrains.annotations.NotNull()
        private final android.widget.TextView attachmentFile = null;
        
        public ViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.View view) {
            super(null);
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.view.View getView() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.TextView getTextView() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.view.ViewGroup getAttachmentMediaContainer() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.ImageView getAttachmentImage() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.VideoView getAttachmentVideo() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.view.View getVideoPlayOverlay() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.ImageView getVideoPlayButton() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.TextView getAttachmentFile() {
            return null;
        }
    }
}