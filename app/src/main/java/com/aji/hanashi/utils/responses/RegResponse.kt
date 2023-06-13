package com.aji.hanashi.utils.responses

import com.google.gson.annotations.SerializedName

data class RegResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
