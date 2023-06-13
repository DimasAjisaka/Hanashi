package com.aji.hanashi.utils.responses

import com.google.gson.annotations.SerializedName

data class GetResponse(
	@field:SerializedName("listStory")
	val listStory: List<ListStoryItem>,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ListStoryItem(
	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("photoUrl")
	val photoUrl: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,
)
