package io.github.nickacpt.stargazer.extensions.commands

import com.kotlindiscord.kord.extensions.checks.inChannel
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.extensions.slashCommandCheck
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.channel.GuildMessageChannel
import io.github.nickacpt.stargazer.db.tables.Channels
import io.github.nickacpt.stargazer.db.tables.CrawlContent
import io.github.nickacpt.stargazer.db.upsert
import io.github.nickacpt.stargazer.slavery.Playground
import io.github.nickacpt.stargazer.slavery.StarboardCrawler
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

class PlaygroundCommandExtension : Extension() {
    override val name: String
        get() = "Playground"

    override suspend fun setup() {
        ephemeralSlashCommand {
            name = "playground"
            description = "Playground command"

            allowUser(Snowflake(318033838330609665u))

            action {
                slashCommandCheck {
                    inChannel(Snowflake(733738771266273350u))
                }

                respond {
                    content = "Playground go Brrrr"
                }


                val messageChannel = event.interaction.getChannel() as? GuildMessageChannel ?: return@action

                messageChannel.guild.channels.collect {
                    transaction {
                        addLogger(StdOutSqlLogger)
                        Channels.upsert {
                            it[Channels.snowflakeId] = messageChannel.guild.id.value.toLong()
                            it[Channels.name] = messageChannel.name
                        }
                    }
                }

                StarboardCrawler.enslaveStarboardChannel(
                    messageChannel,
                    CrawlContent.STARRED_MESSAGES_AND_ATTACHMENTS,
                    1000,
                    Playground::handlePlaygroundMessage
                )
            }

        }
    }
}