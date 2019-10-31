package dev.erickvieira.watermelon.models

data class Chat(
    override var id: String,
    var name: String,
    var description: String?,
    var receivers: List<String>,
    var lastMessage: String,
    var createdBy: String,
    var createdAt: Long,
    var updatedAt: Long?,
    var profilePicture: String?
): IFirebaseRealtimeDatabaseGenericElement