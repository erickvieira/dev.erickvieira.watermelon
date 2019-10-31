package dev.erickvieira.watermelon.models

data class Message(
    override var id: String,
    var createdAt: Long,
    var text: String,
    var image: String?,
    var coordinates: Pair<Double, Double>?,
    var from: String,
    var to: String
): IFirebaseRealtimeDatabaseGenericElement