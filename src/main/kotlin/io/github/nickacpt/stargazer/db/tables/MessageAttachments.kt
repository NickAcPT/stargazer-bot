package io.github.nickacpt.stargazer.db.tables

import org.jetbrains.exposed.sql.Table

object MessageAttachments : TableWithDiscordAuthor, Table() {
    override val authorId = long("author") references Users.id
    val messageId = long("messageId") references Messages.snowflakeId
    val attachmentId = long("attachmentIndex")
    val content = binary("content")

    override val primaryKey = PrimaryKey(messageId, attachmentId)
}