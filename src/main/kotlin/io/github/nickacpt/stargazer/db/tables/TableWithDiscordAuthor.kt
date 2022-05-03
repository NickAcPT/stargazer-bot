package io.github.nickacpt.stargazer.db.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

interface TableWithDiscordAuthor {
    val authorId: Column<Long>
}