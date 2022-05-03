package io.github.nickacpt.stargazer.slavery

import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.User
import io.github.nickacpt.stargazer.db.tables.MessageAttachments
import io.github.nickacpt.stargazer.db.tables.MessageStarReactions
import io.github.nickacpt.stargazer.db.tables.Messages
import io.github.nickacpt.stargazer.db.tables.Users
import io.github.nickacpt.stargazer.db.upsert
import kotlinx.datetime.toJavaInstant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URL

object ModelUtils {

    suspend fun messageToMessageModel(
        message: Message, authorModel: Long
    ): Long {
        val messageModelId = newSuspendedTransaction {
            Messages.upsert {
                it[snowflakeId] = message.id.value.toLong()
                it[channelId] = message.channel.id.value.toLong()
                it[content] = message.content
                it[authorId] = authorModel
                it[starCount] = message.reactions.firstOrNull { r -> r.emoji.name == ("⭐") }?.count ?: 0
                it[timestamp] = message.timestamp.toJavaInstant()
            } get Messages.snowflakeId
        }

        storeMessageAttachments(message, messageModelId, authorModel)

        message.referencedMessage?.let { repliedMessage ->
            storeMessageReply(message, repliedMessage)
        }

        return messageModelId
    }

    private suspend fun storeMessageAttachments(message: Message, l: Long, authorModel: Long) {
        if (message.attachments.isNotEmpty()) println("Storing attachments from message ${message.id}.")

        message.attachments.forEach { attachment ->
            println("Reading attachment with url ${attachment.url}.")
            val attachmentBytes =
                kotlin.runCatching { URL(attachment.proxyUrl).readBytes() }.getOrNull() ?: return@forEach

            MessageAttachments.upsert {
                it[MessageAttachments.messageId] = l
                it[attachmentId] = attachment.id.value.toLong()
                it[authorId] = authorModel
                it[MessageAttachments.content] = attachmentBytes
            }


            message.getReactors(ReactionEmoji.Unicode("⭐")).collect { user ->
                transaction {
                    addLogger(StdOutSqlLogger)

                    val userToUserModel = ModelUtils.userToUserModel(user)
                    val msgId = message.id.value.toLong()
                    if (MessageStarReactions.select { (MessageStarReactions.messageId eq msgId) and (MessageStarReactions.authorId eq userToUserModel) }
                            .empty()) {
                        MessageStarReactions.insert {
                            it[MessageStarReactions.messageId] = msgId
                            it[authorId] = userToUserModel
                        }
                    }

                }
            }

        }
    }

    suspend fun storeMessageReply(message: Message, repliedMessage: Message) {
        val messageToMessageModel =
            newSuspendedTransaction { messageToMessageModel(repliedMessage, userToUserModel(repliedMessage.author)) }

        newSuspendedTransaction {
            addLogger(StdOutSqlLogger)

            Messages.update({ Messages.snowflakeId eq message.id.value.toLong() }) {
                it[replyTo] = messageToMessageModel
            }
        }
    }

    fun userToUserModel(user: User?): Long {
        val notExists = Users.select {
            Users.id eq (user?.id?.value?.toLong() ?: 0)
        }.empty()

        if (!notExists) {
            return (user?.id?.value?.toLong() ?: 0)
        }

        return Users.insert {
            it[id] = user?.id?.value?.toLong() ?: 0
            it[username] = user?.username ?: "Unknown"
            it[discriminator] = user?.discriminator ?: "Unknown"
        } get Users.id
    }

}