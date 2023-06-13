package com.aji.hanashi

import com.aji.hanashi.utils.responses.ListStoryItem

object Fake {
    fun fake() : List<ListStoryItem> {
        val story: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0 ..10) {
            val data = ListStoryItem (
                i.toString(),
                "photo $i",
                "name $i",
                "date $i"
                )
            story.add(data)
        }
        return story
    }
}