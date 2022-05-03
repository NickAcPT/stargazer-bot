package io.github.nickacpt.stargazer.extensions.commands

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.types.edit
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.rest.builder.message.modify.embed
import io.github.nickacpt.stargazer.db.tables.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class TransparencyReportExtension : Extension() {
    override val name: String
        get() = "transparency-report"

    override suspend fun setup() {
        ephemeralSlashCommand {
            name = "sgtransparencyreport"
            description = "Displays a Transparency Report for collected data"

            this.action {
                val user = getUser().asUser()

                respond { content = "Generating Transparency Report..." }

                edit {
                    content = null
                    embed {
                        title = "Transparency Report"
                        this.description =
                            "This is a transparency report for the data collected by the Stargazer bot.\n"

                        transaction {
                            addLogger(StdOutSqlLogger)

                            arrayOf("Global" to null, "User" to user).forEach { (name, user) ->
                                val query: (t: TableWithDiscordAuthor) -> Query = { table ->
                                    if (table !is Table) throw IllegalArgumentException("Table must be a Table")
                                    user?.let { u -> table.select { table.authorId eq u.id.value.toLong() } }
                                        ?: table.selectAll()
                                }

                                field("$name messages count", true) {
                                    query(Messages).count().toString()
                                }

                                field("$name attachments count", true) {
                                    query(MessageAttachments).count().toString()
                                }
                            }

                            field("Total message crawls", false) {
                                CrawlLogs.select {
                                    (CrawlLogs.crawlContent greaterEq CrawlContent.MESSAGES_LINKED_IN_STARBOARD) and (CrawlLogs.totalMessages neq 0)
                                }.count().toString()
                            }
                        }
                    }
                }
            }
        }
    }
}