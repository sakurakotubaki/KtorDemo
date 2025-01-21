package com.example.ktordemo.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktordemo.domain.Post
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MainViewModel : ViewModel() {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
                useArrayPolymorphism = true
            })
        }
    }
    
    var posts by mutableStateOf<List<Post>>(emptyList())
        private set
        
    var selectedPost by mutableStateOf<Post?>(null)
        private set
        
    var isLoading by mutableStateOf(false)
        private set
        
    var error by mutableStateOf<String?>(null)
        private set

    fun fetchPosts() {
        viewModelScope.launch {
            try {
                isLoading = true
                error = null
                posts = client.get("https://jsonplaceholder.typicode.com/posts").body()
            } catch (e: Exception) {
                error = e.message
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        client.close()
    }
}