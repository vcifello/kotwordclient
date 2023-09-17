package com.vcifello.kotwordclient

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

lateinit var cfg: Config

@Serializable
data class Config(
    val nyts: String = "",
    val email: List<String> = emptyList(),
    val sender: String = "",
    val senderPass: String = ""
)

private val json1: Json = Json { ignoreUnknownKeys = true }

 fun loadConfig() {
    val json = File("config.j" +
            "son").readText()
    cfg = json1.decodeFromString<Config>(json)
    //return json1.decodeFromString<Config>(json)
}