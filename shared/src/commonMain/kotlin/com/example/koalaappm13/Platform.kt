package com.example.koalaappm13

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform