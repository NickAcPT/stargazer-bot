package io.github.nickacpt.stargazer

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import io.github.nickacpt.stargazer.db.DatabaseHelper
import io.github.nickacpt.stargazer.extensions.NickNameSelfExtension
import io.github.nickacpt.stargazer.extensions.commands.EnslaveStarboardExtension
import io.github.nickacpt.stargazer.extensions.commands.PlaygroundCommandExtension
import io.github.nickacpt.stargazer.extensions.commands.TransparencyReportExtension

@OptIn(PrivilegedIntent::class)
suspend fun main() {
    DatabaseHelper.initDatabase()

    val token = env("DISCORD_TOKEN")

    val bot = ExtensibleBot(token) {
        cache {
            defaultStrategy = EntitySupplyStrategy.cachingRest
        }

        intents {
            +Intent.GuildMembers
            +Intent.GuildMessages
            +Intent.MessageContent
        }
        presence { watching("Your Starred Messages") }

        applicationCommands {
            this.defaultGuild(726407688879341658u)
        }

        extensions {
            add(::TransparencyReportExtension)
            add(::NickNameSelfExtension)
            add(::EnslaveStarboardExtension)
            add(::PlaygroundCommandExtension)
        }
    }

    bot.start()

}