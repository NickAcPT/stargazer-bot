package io.github.nickacpt.stargazer.db.tables

import org.jetbrains.exposed.sql.Table

object MessageStarReactions : TableWithDiscordAuthor, Table() {
    override val authorId = long("author") references Users.id
    val messageId = long("message") references Messages.snowflakeId

    override val primaryKey = PrimaryKey(authorId, messageId)
}