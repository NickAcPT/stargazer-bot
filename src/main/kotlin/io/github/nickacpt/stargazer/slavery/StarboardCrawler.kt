package io.github.nickacpt.stargazer.slavery

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel
import io.github.nickacpt.stargazer.db.tables.CrawlContent
import io.github.nickacpt.stargazer.db.tables.CrawlLogs
import io.github.nickacpt.stargazer.duckLordSnowflake
import io.github.nickacpt.stargazer.slavery.ModelUtils.messageToMessageModel
import io.github.nickacpt.stargazer.slavery.ModelUtils.storeMessageReply
import io.github.nickacpt.stargazer.slavery.ModelUtils.userToUserModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.atomic.AtomicInteger

object StarboardCrawler {

    suspend fun enslaveStarboardChannel(
        channel: MessageChannel,
        content: CrawlContent,
        maxMessages: Int,
        handler: (suspend (starboardMessage: Message, content: CrawlContent, messageCount: AtomicInteger) -> Unit)? = null
    ) {
        val messageCount = AtomicInteger(0)
        try {
            channel.getMessagesBefore(Snowflake.max).take(maxMessages).filter { it.author?.id == duckLordSnowflake }
                .collect {
                    if (handler != null) handler(it, content, messageCount) else handleMessage(
                        it, content, messageCount
                    )
                }
        } finally {
            transaction {
                addLogger(StdOutSqlLogger)
                CrawlLogs.insert {
                    it[totalMessages] = messageCount.get()
                    it[crawlContent] = content
                }
            }
        }
    }

    private suspend fun handleMessage(
        starboardMessage: Message, content: CrawlContent, messageCount: AtomicInteger
    ) {
        if (content < CrawlContent.STARBOARD_LINKS) return

        val parsedLink = CrawlerUtils.parseDiscordMessageLink(starboardMessage.content) ?: return
        val (channelId, messageId) = parsedLink

        if (content < CrawlContent.MESSAGES_LINKED_IN_STARBOARD) return
        val channel = starboardMessage.getGuildOrNull()?.getChannel(channelId) as? MessageChannel ?: return

        val message = channel.getMessageOrNull(messageId) ?: return
        println("${starboardMessage.id} -> ${message.id}")

        if (content < CrawlContent.STARRED_MESSAGES_AND_ATTACHMENTS) return
        val authorModel = userToUserModel(message.author)

        val sqlMessageId = messageToMessageModel(message, authorModel)

        println("Stored message ${message.id} successfully.")

        val repliedMessage = message.referencedMessage
        if (repliedMessage != null) {
            storeMessageReply(message, repliedMessage)
            println("Stored reply ${repliedMessage.id} from message ${message.id} successfully.")
        }

        if (message.attachments.isNotEmpty()) println("Stored attachments from message ${message.id} successfully.")

        println("Processed ${messageCount.incrementAndGet()} messages so far..")
    }

}