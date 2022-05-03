package io.github.nickacpt.stargazer.slavery

import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel
import io.github.nickacpt.stargazer.db.tables.CrawlContent
import java.util.concurrent.atomic.AtomicInteger

object Playground {
    @JvmStatic
    suspend fun handlePlaygroundMessage(
        starboardMessage: Message, content: CrawlContent, messageCount: AtomicInteger
    ) {
        val parsedLink = CrawlerUtils.parseDiscordMessageLink(starboardMessage.content) ?: return
        val (channelId, messageId) = parsedLink

        val channel = starboardMessage.getGuildOrNull()?.getChannel(channelId) as? MessageChannel ?: return

        val message = channel.getMessageOrNull(messageId) ?: return

        /* ... */

        messageCount.incrementAndGet()
    }
}