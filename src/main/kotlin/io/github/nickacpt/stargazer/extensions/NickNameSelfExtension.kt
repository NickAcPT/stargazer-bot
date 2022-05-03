package io.github.nickacpt.stargazer.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension

class NickNameSelfExtension : Extension() {
    override val name: String
        get() = "name-self"

    override suspend fun setup() {
        kord.guilds.collect { guild ->
            guild.editSelfNickname("Stargazer - Envslaved Starboard")
        }
    }
}