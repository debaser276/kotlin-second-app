package ru.netology.secondapp.dto

import java.lang.IllegalStateException

enum class MediaType {
    IMAGE
}

data class AttachmentModel(val id: String, val url: String, val type: MediaType)

enum class PostType {
    POST, VIDEO, EVENT, AD, REPOST
}

data class Location(val lat: Double, val lng: Double)

infix fun Double.x(that: Double) = Location(this, that)

data class PostModel (
    val id: Int,
    val authorId: Int,
    val author: String,
    val created: Long,
    val content: String?,
    val address: String? = null,
    val loc: Location? = null,
    val link: String? = null,
    val sourceId: Int? = null,
    var likes: Int = 0,
    var likedSet: MutableSet<Int> = mutableSetOf(),
    var reposts: Int = 0,
    var repostedSet: MutableSet<Int> = mutableSetOf(),
    var shares: Int = 0,
    var sharedByMe: Boolean = false,
    var views: Int = 0,
    val type: PostType = PostType.POST,
    val attachment: AttachmentModel? = null
) {
    var likeActionPerforming = false

    fun updateLikes(updateModel: PostModel) {
        if (id != updateModel.id) throw IllegalStateException()
        likes = updateModel.likes
        likedSet = updateModel.likedSet
        reposts = updateModel.reposts
        repostedSet = updateModel.repostedSet
    }
}