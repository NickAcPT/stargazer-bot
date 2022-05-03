package io.github.nickacpt.stargazer.extensions.commands

import com.kotlindiscord.kord.extensions.checks.inChannel
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.converters.impl.enumChoice
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.extensions.slashCommandCheck
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.common.entity.Snowflake
import io.github.nickacpt.stargazer.db.tables.CrawlContent
import io.github.nickacpt.stargazer.slavery.StarboardCrawler


class EnslaveStarboardExtension : Extension() {
    inner class EnslaveArguments : Arguments() {
        val target by enumChoice<CrawlContent> {
            name = "content"
            description = "The content to enslave"
            typeName = "content"
        }
    }


    override val name: String
        get() = "enslave-starboard"

    override suspend fun setup() {
        ephemeralSlashCommand(::EnslaveArguments) {
            name = "enslave-starboard"
            description = "Enslave the starboard"

            allowUser(Snowflake(318033838330609665u))

            action {
                slashCommandCheck {
                    inChannel(Snowflake(733738771266273350u))
                }

                respond {
                    content = "Starting the enslavement process"
                }

                val messageChannel = event.interaction.getChannel()
                StarboardCrawler.enslaveStarboardChannel(messageChannel, arguments.target, 1000)
            }
        }
    }
}