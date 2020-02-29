package ru.netology.secondapp.dto

import java.lang.IllegalStateException

enum class MediaType {
    IMAGE
}

data class MediaModel(val id: String, val url: String, val type: MediaType)

enum class PostType {
    POST, REPOST, VIDEO, EVENT, AD
}

class Location(val lat: Double, val lng: Double)

infix fun Double.x(that: Double) = Location(this, that)

data class PostModel (
    val id: Int,
    val author: String,
    val created: Long,
    val content: String?,
    val address: String? = null,
    val loc: Location? = null,
    val link: String? = null,
    val sourceId: Int? = null,
    val media: String? = null,
    val mediaType: MediaType? = null,
    var likes: Int = 0,
    var likedSet: MutableSet<Int> = mutableSetOf(),
    var reposts: Int = 0,
    var repostedByMe: Boolean = false,
    var shares: Int = 0,
    var sharedByMe: Boolean = false,
    var views: Int = 0,
    val type: PostType = PostType.POST
) {
    var likeActionPerforming = false

    fun updateLikes(updateModel: PostModel) {
        if (id != updateModel.id) throw IllegalStateException()
        likes = updateModel.likes
        likedSet = updateModel.likedSet
    }
}