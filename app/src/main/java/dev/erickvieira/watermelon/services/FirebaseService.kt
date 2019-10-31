package dev.erickvieira.watermelon.services

import com.google.firebase.database.*
import dev.erickvieira.watermelon.models.IFirebaseRealtimeDatabaseGenericElement
import io.reactivex.Observable
import java.lang.Exception


abstract class FirebaseService<T: IFirebaseRealtimeDatabaseGenericElement>(
    private val mainPath: String = "/",
    private var pageSize: Int = 30
) {

    enum class LimitTo {
        LAST,
        FIRST;
    }

    abstract fun parse(snapshot: DataSnapshot): T?

    private val ref: Observable<DatabaseReference> get() = Observable.just(
        FirebaseDatabase.getInstance().reference.child(mainPath)
    )

    private fun getAll(limimtTo: LimitTo = LimitTo.LAST): Observable<List<T>> {
        return Observable.create { emitter ->
            try {
                ref.subscribe {
                    (if (limimtTo == LimitTo.FIRST) {
                        it.orderByKey().limitToFirst(pageSize)
                    } else {
                        it.orderByKey().limitToLast(pageSize)
                    }).addValueEventListener(
                        object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                                emitter.onError(error.toException())
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                val list = ArrayList<T>()
                                var currentElement: T?
                                for (child: DataSnapshot in snapshot.children) {
                                    currentElement = parse(child)
                                    if (currentElement != null) {
                                        list.add(currentElement)
                                    }
                                }
                                emitter.onNext(list)
                                emitter.onComplete()
                            }
                        }
                    )
                }
            } catch (exc: Exception) {
                emitter.onError(exc)
            }
        }
    }

    private fun append(element: T): Observable<T> = Observable.create { emitter ->
        try {
            ref.subscribe {
                val messageId = it.push().key!!
                element.id = messageId
                it.child(messageId).setValue(element)
                emitter.onNext(element)
                emitter.onComplete()
            }
        } catch (exc: Exception) {
            emitter.onError(exc)
        }
    }

}