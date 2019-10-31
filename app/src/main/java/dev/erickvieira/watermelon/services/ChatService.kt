package dev.erickvieira.watermelon.services

import com.google.firebase.database.*
import dev.erickvieira.watermelon.models.Chat
import dev.erickvieira.watermelon.models.Message
import io.reactivex.Observable
import java.lang.Exception

fun getMessagesRef(userId: String, chatId: String): Observable<DatabaseReference> {
    return Observable.create<DatabaseReference> { emitter ->
        try {
            val ref: DatabaseReference? = FirebaseDatabase.getInstance().reference.child("/users/$userId/chats/$chatId/messages")
            if (ref != null) {
                emitter.onNext(ref)
            } else emitter.onError(NoSuchElementException("No chats found for chat $userId:$chatId"))
            emitter.onComplete()
        } catch (exc: Exception) {
            emitter.onError(exc)
        }
    }
}

fun sendMessage(message: Message): Observable<Message> {
    return Observable.create<Message> { emitter ->
        try {
            getMessagesRef(message.from, message.to).subscribe {
                val messageId = it.push().key!!
                message.id = messageId
                it.child(messageId).setValue(message)
                emitter.onNext(message)
                emitter.onComplete()
            }
        } catch (exc: Exception) {
            emitter.onError(exc)
        }
    }
}

fun getChatMessages(userId: String, chatId: String): Observable<List<Message>> {
    return Observable.create<List<Message>> { emitter ->
        try {
            getMessagesRef(userId, chatId).subscribe {
                it.addValueEventListener(object: ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        emitter.onError(error.toException())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val messages = ArrayList<Message>()
                        var currentMessage: Message?
                        for (child: DataSnapshot in snapshot.children) {
                            currentMessage = child.getValue(Message::class.java)
                            if (currentMessage != null) {
                                messages.add(currentMessage)
                            }
                        }
                        emitter.onNext(messages)
                        emitter.onComplete()
                    }
                })
            }
        } catch (exc: Exception) {
            emitter.onError(exc)
        }
    }
}

fun getChatsRef(userId: String): Observable<DatabaseReference> {
    return Observable.create<DatabaseReference> { emitter ->
        try {
            val ref: DatabaseReference? = FirebaseDatabase.getInstance().reference.child("/users/$userId/chats/")
            if (ref != null) {
                emitter.onNext(ref)
            } else emitter.onError(NoSuchElementException("No chats found for user $userId"))
            emitter.onComplete()
        } catch (exc: Exception) {
            emitter.onError(exc)
        }
    }
}

fun createChat(userId: String, chat: Chat): Observable<Chat> {
    return Observable.create<Chat> { emitter ->
        try {
            getChatsRef(userId).subscribe {
                val chatId = it.push().key!!
                chat.id = chatId
                it.child(chatId).setValue(chat)
                emitter.onNext(chat)
                emitter.onComplete()
            }
        } catch (exc: Exception) {
            emitter.onError(exc)
        }
    }
}

fun getChats(userId: String): Observable<List<Chat>> {
    return Observable.create<List<Chat>> { emitter ->
        try {
            getChatsRef(userId).subscribe {
                it.addValueEventListener(object: ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        emitter.onError(error.toException())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val chats = ArrayList<Chat>()
                        var currentChat: Chat?
                        for (child: DataSnapshot in snapshot.children) {
                            currentChat = child.getValue(Chat::class.java)
                            if (currentChat != null) {
                                chats.add(currentChat)
                            }
                        }
                        emitter.onNext(chats)
                        emitter.onComplete()
                    }
                })
            }
        } catch (exc: Exception) {
            emitter.onError(exc)
        }
    }
}