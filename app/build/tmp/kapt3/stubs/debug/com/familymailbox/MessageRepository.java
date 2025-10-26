package com.familymailbox;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\u000eR\u001d\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/familymailbox/MessageRepository;", "", "messageDao", "Lcom/familymailbox/MessageDao;", "(Lcom/familymailbox/MessageDao;)V", "allMessages", "Landroidx/lifecycle/LiveData;", "", "Lcom/familymailbox/Message;", "getAllMessages", "()Landroidx/lifecycle/LiveData;", "insert", "", "message", "(Lcom/familymailbox/Message;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class MessageRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.familymailbox.MessageDao messageDao = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.familymailbox.Message>> allMessages = null;
    
    public MessageRepository(@org.jetbrains.annotations.NotNull()
    com.familymailbox.MessageDao messageDao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.familymailbox.Message>> getAllMessages() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.familymailbox.Message message, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}