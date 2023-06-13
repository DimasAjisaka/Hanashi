package com.aji.hanashi.repositories

sealed class Source<out R> private constructor(){
    data class Success<out T> (val data: T): Source<T>()
    data class Error(val message: String): Source<Nothing>()
    object Loading: Source<Nothing>()
}