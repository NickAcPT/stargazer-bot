package io.github.nickacpt.stargazer.slavery

import dev.kord.common.entity.Snowflake

object CrawlerUtils {

    fun parseDiscordMessageLink(link: String): Pair<Snowflake, Snowflake>? {
        // Parse link as the following:
        // https://discord.com/channels/<ignored>/<channel id>/<message id>

        if (link.startsWith("https://discord.com/channels")) {
            val split = link.removePrefix("https://discord.com/channels/").split("/")
            if (split.size == 3) {
                return Snowflake(split[1].toLong()) to Snowflake(split[2].toLong())
            }
        }

        return null
    }
}