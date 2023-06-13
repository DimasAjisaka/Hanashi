package com.aji.hanashi.utils.responses

import com.google.gson.annotations.SerializedName

data class GetLocStoryResponse(

	@field:SerializedName("listStory")
	val listStory: List<ListLocItem>,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ListLocItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("lon")
	val lon: Double? = null,

	@field:SerializedName("lat")
	val lat: Double? = null
)
