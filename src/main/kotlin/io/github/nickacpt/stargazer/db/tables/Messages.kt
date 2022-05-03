package io.github.nickacpt.stargazer.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Messages : TableWithDiscordAuthor, Table() {
    override val authorId = long("author") references Users.id
    val snowflakeId = long("snowflake").index()
    val channelId = long("channelId").index()
    val content = text("content").nullable().default(null)
    val starCount = integer("starCount").default(0)
    val timestamp = timestamp("timestamp")
    val replyTo = (long("replyTo") references Messages.snowflakeId).nullable()

    override val primaryKey = PrimaryKey(snowflakeId)
}