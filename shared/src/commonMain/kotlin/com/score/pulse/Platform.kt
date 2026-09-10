package com.score.pulse

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform