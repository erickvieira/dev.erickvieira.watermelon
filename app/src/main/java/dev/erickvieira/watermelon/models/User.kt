package dev.erickvieira.watermelon.models

data class User(
    override var id: String,
    var authId: String,
    var name: String,
    var email: String,
    var profilePicture: String?,
    var lastLogin: Long,
    var createdAt: Long,
    var updatedAt: Long?,
    var active: Boolean,
    var status: String?,
    var location: Pair<Double, Double>?
): IFirebaseRealtimeDatabaseGenericElement